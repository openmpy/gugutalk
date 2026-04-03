package com.pidulgi.server.chat.dto.request

data class MessageSendMediaRequest(

    val imageKeys: List<String> = emptyList(),
    val videoKeys: List<String> = emptyList(),
)