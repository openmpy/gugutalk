package com.pidulgi.server.admin.dto.request

data class AdminLoginRequest(

    val phoneNumber: String,
    val password: String,
)