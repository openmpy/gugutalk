package com.pidulgi.server.auth.dto.request

data class LoginRequest(

    val uuid: String,
    val phoneNumber: String,
    val password: String,
)
