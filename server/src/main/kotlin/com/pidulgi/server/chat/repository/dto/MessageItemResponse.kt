package com.pidulgi.server.chat.repository.dto

import com.pidulgi.server.chat.entity.type.MessageType
import java.time.LocalDateTime

data class MessageItemResponse(

    val messageId: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val targetId: Long,
    val content: String,
    val type: MessageType,
    val createdAt: LocalDateTime,
)
