package com.pidulgi.server.auth.dto.request

data class LogoutRequest(

    val accessToken: String,
    val refreshToken: String,
)
