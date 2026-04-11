package com.pidulgi.server.chat.service.event

data class ChatDeleteEvent(

    val chatRoomId: Long,
    val targetId: Long,
)
