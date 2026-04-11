package com.pidulgi.server.chat.repository.impl

import com.pidulgi.server.chat.repository.ChatRoomCustomRepository
import com.pidulgi.server.chat.repository.result.ChatRoomItemResult
import com.pidulgi.server.chat.repository.result.ChatRoomSearchItemResult
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChatRoomCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : ChatRoomCustomRepository {

    // 채팅방 목록
    override fun findAllChatRoomsByCursor(
        memberId: Long,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResult> {
        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                COALESCE(cr.last_message_at, cr.created_at) < :cursorDate
                OR (COALESCE(cr.last_message_at, cr.created_at) = :cursorDate AND cr.id < :cursorId)
            )
            """.trimIndent()
        } else ""

        val statusCondition = if (status.equals("UNREAD", ignoreCase = true)) {
            """
            AND (
                (cr.member1_id = :memberId AND cr.member1_unread_count > 0)
                OR
                (cr.member2_id = :memberId AND cr.member2_unread_count > 0)
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
                END AS unread_count
            FROM chat_room cr
            JOIN member m 
                ON m.id = CASE 
                    WHEN cr.member1_id = :memberId THEN cr.member2_id
                    ELSE cr.member1_id
                END
                AND m.deleted_at IS NULL
            WHERE cr.deleted_at IS NULL
                AND (cr.member1_id = :memberId OR cr.member2_id = :memberId)
                $cursorCondition
                $statusCondition
            ORDER BY sort_at DESC, cr.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("memberId", memberId)
            setParameter("size", size)

            if (cursorId != null && cursorDate != null) {
                setParameter("cursorId", cursorId)
                setParameter("cursorDate", cursorDate)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toChatRoomItemResult)
    }

    // 채팅방 검색 - 닉네임 유사도
    override fun findAllChatRoomsByNicknameWithCursor(
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

    private fun toChatRoomItemResult(row: Array<Any?>) =
        ChatRoomItemResult(
            chatRoomId = (row[0] as Number).toLong(),
            targetId = (row[1] as Number).toLong(),
            nickname = row[2] as String,
            profileKey = row[3] as String?,
            lastMessage = row[4] as String?,
            sortAt = row[5] as LocalDateTime,
            unreadCount = (row[6] as Number).toInt(),
        )

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