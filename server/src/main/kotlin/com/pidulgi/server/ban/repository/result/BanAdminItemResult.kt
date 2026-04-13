package com.pidulgi.server.ban.repository.result

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class BanAdminItemResult(

    val banId: Long,
    val type: ReportType,
    val uuid: String,
    val reason: String?,
    val createdAt: LocalDateTime,
    val expiredAt: LocalDateTime,
)
