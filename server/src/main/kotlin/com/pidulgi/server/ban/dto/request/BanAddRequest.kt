package com.pidulgi.server.ban.dto.request

import java.time.LocalDateTime

data class BanAddRequest(

    val uuid: String,
    val phoneNumber: String,
    val type: String,
    val reason: String?,
    val expiredAt: LocalDateTime,
)
