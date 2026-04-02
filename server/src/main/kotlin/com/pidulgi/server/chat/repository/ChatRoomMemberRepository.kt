package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.ChatRoomMember
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long> {

    fun findByChatRoomIdAndMemberId(chatRoomId: Long, memberId: Long): ChatRoomMember?

    fun findAllByChatRoomId(chatRoomId: Long): List<ChatRoomMember>
}