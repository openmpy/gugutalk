package com.pidulgi.server.chat.dto.response

import java.time.LocalDateTime

data class ChatRoomGetResponse(

    val chatRoomId: Long,
    val memberId: Long,
    val profileUrl: String?,
    val nickname: String,
    val lastMessage: String?,
    val lastMessageAt: LocalDateTime?,
)
