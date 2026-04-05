package com.pidulgi.server.auth.dto.request

data class RotateTokenRequest(

    val memberId: Long,
    val refreshToken: String,
)