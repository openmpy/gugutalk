package com.pidulgi.server.auth.dto.request

data class LoginRequest(

    val phoneNumber: String,
    val password: String,
)
