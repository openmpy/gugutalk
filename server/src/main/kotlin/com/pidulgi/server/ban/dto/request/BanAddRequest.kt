package com.pidulgi.server.ban.dto.request

data class BanAddRequest(

    val uuid: String,
    val phoneNumber: String,
    val type: String,
    val reason: String?,
    val days: Long,
)
