package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {

    fun findByMember1IdAndMember2Id(memberId: Long, member2Id: Long): ChatRoom?
}