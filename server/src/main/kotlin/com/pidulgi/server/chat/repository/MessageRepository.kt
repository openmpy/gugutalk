package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<Message, Long>, MessageCustomRepository {

    @Query(
        value = """
            SELECT m.id 
            FROM Message m
            WHERE m.chatRoom.id = :chatRoomId
            ORDER BY m.id DESC
            LIMIT 1
        """
    )
    fun findLastMessageId(chatRoomId: Long): Long?
}