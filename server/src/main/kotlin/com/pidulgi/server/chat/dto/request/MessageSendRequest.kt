package com.pidulgi.server.chat.dto.request

import com.pidulgi.server.chat.entity.type.MessageType

data class MessageSendRequest(

    val content: String,
    val type: MessageType,
)