package com.pidulgi.server.chat.service.event

import com.pidulgi.server.chat.dto.event.ChatEvent

data class ChatTopicEvent(

    val chatRoomId: Long,
    val chatEvent: ChatEvent
)
