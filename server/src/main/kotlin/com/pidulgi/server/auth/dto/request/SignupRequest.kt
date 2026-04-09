package com.pidulgi.server.auth.dto.request

data class SignupRequest(

    val uuid: String,
    val phoneNumber: String,
    val verificationCode: String,
    val password: String,
    val gender: String,
)
