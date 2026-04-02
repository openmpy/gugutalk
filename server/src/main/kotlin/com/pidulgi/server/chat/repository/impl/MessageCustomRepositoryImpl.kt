package com.pidulgi.server.chat.repository.impl

import com.pidulgi.server.chat.entity.type.MessageType
import com.pidulgi.server.chat.repository.MessageCustomRepository
import com.pidulgi.server.chat.repository.dto.MessageItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MessageCustomRepositoryImpl(
    private val entityManager: EntityManager,
) : MessageCustomRepository {

    override fun findMessagesByCursor(
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MessageItemResponse> {
        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                m.created_at < :cursorDate
                OR (
                    m.created_at = :cursorDate
                    AND m.id < :cursorId
                )
            )
            """.trimIndent()
        } else ""

        val sql = """
            SELECT
                m.id,
                m.sender_id,
                m.content,
                m.type,
                m.created_at
            FROM message m
            WHERE m.chat_room_id = :chatRoomId
                $cursorCondition
            ORDER BY m.created_at DESC, m.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("chatRoomId", chatRoomId)
            setParameter("size", size)

            if (cursorId != null && cursorDate != null) {
                setParameter("cursorId", cursorId)
                setParameter("cursorDate", cursorDate)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMessageItemResponse)
    }

    private fun toMessageItemResponse(row: Array<Any?>) = MessageItemResponse(
        messageId = (row[0] as Number).toLong(),
        senderId = (row[1] as Number).toLong(),
        content = row[2] as String,
        type = MessageType.valueOf(row[3] as String),
        createdAt = row[4] as LocalDateTime,
    )
}