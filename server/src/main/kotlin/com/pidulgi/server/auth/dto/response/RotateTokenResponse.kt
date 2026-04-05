package com.pidulgi.server.auth.dto.response

data class RotateTokenResponse(

    val accessToken: String,
    val refreshToken: String,
)
