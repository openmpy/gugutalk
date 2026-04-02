package com.pidulgi.server.chat.repository.dto

import java.time.LocalDateTime

data class ChatRoomItemResponse(

    val chatRoomId: Long,
    val memberId: Long,
    val profileKey: String?,
    val nickname: String,
    val lastMessage: String?,
    val lastMessageAt: LocalDateTime?,
)
