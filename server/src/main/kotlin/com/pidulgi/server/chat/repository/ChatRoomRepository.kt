package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ChatRoomRepository : JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository {

    fun findByMember1IdAndMember2Id(memberId: Long, member2Id: Long): ChatRoom?

    @Modifying
    @Query(
        value = """
            UPDATE ChatRoom cr
            SET 
                cr.member1UnreadCount = CASE 
                    WHEN cr.member1Id = :targetId THEN cr.member1UnreadCount + 1
                    ELSE cr.member1UnreadCount
                END,
                cr.member2UnreadCount = CASE 
                    WHEN cr.member2Id = :targetId THEN cr.member2UnreadCount + 1
                    ELSE cr.member2UnreadCount
                END
            WHERE cr.id = :chatRoomId
        """
    )
    fun increaseUnreadCount(chatRoomId: Long, targetId: Long)
}