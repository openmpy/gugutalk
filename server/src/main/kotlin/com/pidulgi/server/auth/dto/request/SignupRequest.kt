package com.pidulgi.server.auth.dto.request

import com.pidulgi.server.member.entity.type.Gender

data class SignupRequest(

    val phoneNumber: String,
    val verificationCode: String,
    val password: String,
    val gender: Gender,
)
