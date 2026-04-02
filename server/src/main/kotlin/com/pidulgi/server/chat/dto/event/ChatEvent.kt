package com.pidulgi.server.chat.dto.event

import com.pidulgi.server.chat.dto.event.type.ChatEventType

data class ChatEvent(

    val eventType: ChatEventType,
    val payload: Any?,
)
