package com.pidulgi.server.auth.dto.response

data class LoginResponse(

    val memberId: Long,
    val accessToken: String,
    val refreshToken: String,
)
