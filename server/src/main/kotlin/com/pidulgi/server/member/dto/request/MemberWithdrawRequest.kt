package com.pidulgi.server.member.dto.request

data class MemberWithdrawRequest(

    val accessToken: String,
    val refreshToken: String,
)
