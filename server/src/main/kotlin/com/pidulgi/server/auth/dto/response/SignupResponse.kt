package com.pidulgi.server.auth.dto.response

data class SignupResponse(

    val memberId: Long,
    val accessToken: String,
    val refreshToken: String,
)
