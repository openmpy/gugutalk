package com.pidulgi.server.chat.service.extension

import com.pidulgi.server.chat.dto.response.MessageGetResponse
import com.pidulgi.server.chat.entity.type.MessageType
import com.pidulgi.server.chat.repository.result.MessageItemResult

fun MessageItemResult.toMessageGetResponse(endpoint: String) = MessageGetResponse(

    messageId = this.messageId,
    senderId = this.senderId,
    content = when (this.type) {
        MessageType.IMAGE, MessageType.VIDEO -> "$endpoint${this.content}"
        else -> this.content
    },
    type = this.type,
    createdAt = this.createdAt,
)