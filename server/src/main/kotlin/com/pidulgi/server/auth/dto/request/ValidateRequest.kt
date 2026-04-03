package com.pidulgi.server.auth.dto.request

data class ValidateRequest(

    val nickname: String,
    val birthYear: Int,
)