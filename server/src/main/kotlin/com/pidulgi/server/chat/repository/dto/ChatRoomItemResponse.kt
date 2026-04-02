package com.pidulgi.server.chat.repository.dto

import java.time.LocalDateTime

data class ChatRoomItemResponse(

    val chatRoomId: Long,
    val targetId: Long,
    val nickname: String,
    val profileKey: String?,
    val lastMessage: String?,
    val lastMessageAt: LocalDateTime?,
    val sortAt: LocalDateTime,
    val unreadCount: Int,
)
