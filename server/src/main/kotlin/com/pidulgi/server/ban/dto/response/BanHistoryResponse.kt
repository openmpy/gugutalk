package com.pidulgi.server.ban.dto.response

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class BanHistoryResponse(

    val type: ReportType,
    val phoneNumber: String,
    val reason: String?,
    val createdAt: LocalDateTime,
    val expiredAt: LocalDateTime,
)
