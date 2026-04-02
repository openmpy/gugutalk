package com.pidulgi.server.chat.dto.response

data class ChatRoomGetTargetResponse(

    val memberId: Long,
    val profileUrl: String?,
    val nickname: String,
)
