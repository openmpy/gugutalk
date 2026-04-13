package com.pidulgi.server.ban.dto.response

import java.time.LocalDateTime

data class BanGetResponse(

    val banId: Long,
    val uuid: String,
    val reason: String?,
    val createdAt: LocalDateTime,
    val expiredAt: LocalDateTime,
)
