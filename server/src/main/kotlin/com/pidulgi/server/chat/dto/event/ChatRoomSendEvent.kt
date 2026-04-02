package com.pidulgi.server.chat.dto.event

import com.pidulgi.server.chat.entity.type.MessageType
import java.time.LocalDateTime

data class ChatRoomSendEvent(

    val chatRoomId: Long,
    val senderId: Long,
    val profileUrl: String?,
    val nickname: String,
    val lastMessage: String?,
    val type: MessageType,
    val lastMessageAt: LocalDateTime?,
    val unreadCount: Int,
)
