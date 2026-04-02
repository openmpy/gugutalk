package com.pidulgi.server.chat.dto.response

import java.time.LocalDateTime

data class ChatRoomGetResponse(

    val chatRoomId: Long,
    val targetId: Long,
    val nickname: String,
    val profileUrl: String?,
    val lastMessage: String?,
    val lastMessageAt: LocalDateTime?,
    val sortAt: LocalDateTime,
    val unreadCount: Int,
)
