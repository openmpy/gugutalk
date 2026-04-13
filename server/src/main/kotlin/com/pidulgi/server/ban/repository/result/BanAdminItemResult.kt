package com.pidulgi.server.ban.repository.result

import java.time.LocalDateTime

data class BanAdminItemResult(

    val banId: Long,
    val uuid: String,
    val reason: String?,
    val createdAt: LocalDateTime,
    val expiredAt: LocalDateTime,
)
