package com.pidulgi.server.chat.dto.response

import com.pidulgi.server.chat.entity.type.MessageType
import java.time.LocalDateTime

data class MessageSendResponse(

    val messageId: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val content: String,
    val type: MessageType,
    val createdAt: LocalDateTime,
)
