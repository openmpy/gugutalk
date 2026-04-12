package com.pidulgi.server.chat.service.event

import com.pidulgi.server.chat.dto.event.ChatEvent

data class ChatQueueEvent(

    val memberId: Long,
    val chatEvent: ChatEvent
)
