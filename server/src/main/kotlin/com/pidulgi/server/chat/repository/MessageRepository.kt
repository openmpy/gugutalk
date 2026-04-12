package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<Message, Long>, MessageCustomRepository {

    @Query(
        value = """
            SELECT m.id 
            FROM Message m
            WHERE m.chatRoomId = :chatRoomId
            ORDER BY m.id DESC
            LIMIT 1
        """
    )
    fun findLastMessageId(chatRoomId: Long): Long?

    @Query(
        value = """
            SELECT content FROM message
            WHERE chat_room_id IN :chatRoomIds
            AND type IN ('IMAGE', 'VIDEO')
        """,
        nativeQuery = true
    )
    fun findAllKeysByChatRoomIdIn(chatRoomIds: List<Long>): List<String>

    fun deleteAllByChatRoomIdIn(chatRoomIds: List<Long>)
}