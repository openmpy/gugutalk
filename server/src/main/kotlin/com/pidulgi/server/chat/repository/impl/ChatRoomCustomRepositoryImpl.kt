package com.pidulgi.server.chat.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.repository.ChatRoomCustomRepository
import com.pidulgi.server.chat.repository.result.ChatRoomItemResult
import com.pidulgi.server.chat.repository.result.ChatRoomSearchItemResult
import com.pidulgi.server.member.entity.Member
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChatRoomCustomRepositoryImpl(

    private val context: JpqlRenderContext,
    private val entityManager: EntityManager,
) : ChatRoomCustomRepository {

    private val renderer = JpqlRenderer()

    // 채팅방 목록
    override fun findAllChatRoomsByCursor(
        memberId: Long,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResult> {
        val query = jpql {
            val sortAt = coalesce(
                path(ChatRoom::lastMessageAt),
                path(ChatRoom::createdAt)
            )
            val unreadCount = caseWhen(path(ChatRoom::member1Id).eq(memberId))
                .then(path(ChatRoom::member1UnreadCount))
                .`else`(path(ChatRoom::member2UnreadCount))

            selectNew<ChatRoomItemResult>(
                path(ChatRoom::id),
                path(Member::id),
                path(Member::nickname),
                path(Member::profileKey),
                path(ChatRoom::lastMessage),
                sortAt,
                unreadCount,
            ).from(
                entity(ChatRoom::class),
                join(Member::class).on(
                    or(
                        and(
                            path(ChatRoom::member1Id).eq(memberId),
                            path(Member::id).eq(path(ChatRoom::member2Id)),
                        ),
                        and(
                            path(ChatRoom::member2Id).eq(memberId),
                            path(Member::id).eq(path(ChatRoom::member1Id)),
                        ),
                    )
                )
            ).whereAnd(
                if (cursorId != null && cursorDate != null) {
                    or(
                        sortAt.lt(cursorDate),
                        and(
                            sortAt.eq(cursorDate),
                            path(ChatRoom::id).lt(cursorId),
                        ),
                    )
                } else null,

                if (status.equals("UNREAD", ignoreCase = true)) {
                    or(
                        and(
                            path(ChatRoom::member1Id).eq(memberId),
                            path(ChatRoom::member1UnreadCount).gt(0),
                        ),
                        and(
                            path(ChatRoom::member2Id).eq(memberId),
                            path(ChatRoom::member2UnreadCount).gt(0),
                        ),
                    )
                } else null,
            ).orderBy(
                sortAt.desc(),
                path(ChatRoom::id).desc(),
            )
        }

        val rendered = renderer.render(query, context)
        val jpaQuery = entityManager.createQuery(rendered.query, ChatRoomItemResult::class.java).apply {
            rendered.params.forEach { (name, value) ->
                setParameter(name, value)
            }
        }
        return jpaQuery.setMaxResults(size).resultList
    }

    // 채팅방 검색 - 닉네임 유사도
    override fun findAllChatRoomsByNicknameByCursor(
        memberId: Long,
        nickname: String,
        cursorId: Long?,
        cursorSimilarity: Double?,
        size: Int
    ): List<ChatRoomSearchItemResult> {
        val cursorCondition = if (cursorId != null && cursorSimilarity != null) {
            """
            AND (
                similarity(m.nickname, :nickname) < :cursorSimilarity
                OR (similarity(m.nickname, :nickname) = :cursorSimilarity AND m.id < :cursorId)
            )
            """.trimIndent()
        } else ""

        val sql = """
            SELECT 
                cr.id,
                m.id AS target_id,
                m.nickname,
                m.profile_key,
                cr.last_message,
                COALESCE(cr.last_message_at, cr.created_at) AS sort_at,
                CASE 
                    WHEN cr.member1_id = :memberId THEN cr.member1_unread_count
                    ELSE cr.member2_unread_count
                END AS unread_count,
                similarity(m.nickname, :nickname) AS similarity_score
            FROM chat_room cr
            JOIN member m 
                ON m.id = CASE 
                    WHEN cr.member1_id = :memberId THEN cr.member2_id
                    ELSE cr.member1_id
                END
                AND m.deleted_at IS NULL
            WHERE cr.deleted_at IS NULL
                AND (cr.member1_id = :memberId OR cr.member2_id = :memberId)
                AND m.nickname % :nickname
                $cursorCondition
            ORDER BY similarity_score DESC, cr.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("memberId", memberId)
            setParameter("nickname", nickname)
            setParameter("size", size)

            if (cursorId != null && cursorSimilarity != null) {
                setParameter("cursorId", cursorId)
                setParameter("cursorSimilarity", cursorSimilarity)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toChatRoomSearchItemResult)
    }

    private fun toChatRoomSearchItemResult(row: Array<Any?>) =
        ChatRoomSearchItemResult(
            chatRoomId = (row[0] as Number).toLong(),
            targetId = (row[1] as Number).toLong(),
            nickname = row[2] as String,
            profileKey = row[3] as String?,
            lastMessage = row[4] as String?,
            sortAt = row[5] as LocalDateTime,
            unreadCount = (row[6] as Number).toInt(),
            similarityScore = (row[7] as Number).toDouble(),
        )
}