package com.pidulgi.server.chat.dto.response

import com.pidulgi.server.chat.entity.type.MessageType
import java.time.LocalDateTime

data class MessageGetResponse(

    val messageId: Long,
    val senderId: Long,
    val content: String,
    val type: MessageType,
    val createdAt: LocalDateTime,
)
