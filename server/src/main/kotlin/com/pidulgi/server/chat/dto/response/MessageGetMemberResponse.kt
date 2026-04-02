package com.pidulgi.server.chat.dto.response

data class MessageGetMemberResponse(

    val memberId: Long,
    val profileUrl: String?,
    val nickname: String,
)
