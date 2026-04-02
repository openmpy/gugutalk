package com.pidulgi.server.chat.repository.impl

import com.pidulgi.server.chat.repository.ChatRoomCustomRepository
import com.pidulgi.server.chat.repository.dto.ChatRoomItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChatRoomCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : ChatRoomCustomRepository {

    override fun findChatRoomsByCursor(
        memberId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResponse> {
        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                COALESCE(cr.last_message_at, cr.created_at) < :cursorDate
                OR (
                    COALESCE(cr.last_message_at, cr.created_at) = :cursorDate
                    AND cr.id < :cursorId
                )
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
                cr.last_message_at,
                COALESCE(cr.last_message_at, cr.created_at) AS sort_at
            FROM chat_room cr
            JOIN member m 
                ON m.id = CASE 
                    WHEN cr.member1_id = :memberId THEN cr.member2_id
                    ELSE cr.member1_id
                END
            WHERE cr.deleted_at IS NULL
                AND (cr.member1_id = :memberId OR cr.member2_id = :memberId)
                $cursorCondition
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
        return (query.resultList as List<Array<Any?>>).map(::toChatRoomItemResponse)
    }

    private fun toChatRoomItemResponse(row: Array<Any?>) = ChatRoomItemResponse(
        chatRoomId = (row[0] as Number).toLong(),
        targetId = (row[1] as Number).toLong(),
        nickname = row[2] as String,
        profileKey = row[3] as String?,
        lastMessage = row[4] as String?,
        lastMessageAt = row[5] as LocalDateTime?,
        sortAt = row[6] as LocalDateTime,
    )
}