package com.pidulgi.server.chat.repository.result

import com.pidulgi.server.chat.entity.type.MessageType
import java.time.LocalDateTime

data class MessageItemResult(

    val messageId: Long,
    val senderId: Long,
    val content: String,
    val type: MessageType,
    val createdAt: LocalDateTime,
)
