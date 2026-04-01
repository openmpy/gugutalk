package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.ChatRoom
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {

    @Query(
        value = """
            SELECT r FROM ChatRoom r
            WHERE r.id IN (
              SELECT m.chatRoomId FROM ChatRoomMember m
              WHERE m.memberId IN (:memberId, :targetId)
              GROUP BY m.chatRoomId
              HAVING COUNT(DISTINCT m.memberId) = 2
          )
        """
    )
    fun findDirectRoom(
        @Param("memberId") memberId: Long,
        @Param("targetId") targetId: Long
    ): ChatRoom?
}