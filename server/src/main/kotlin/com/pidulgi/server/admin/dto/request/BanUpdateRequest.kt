package com.pidulgi.server.admin.dto.request

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class BanUpdateRequest(

    val type: ReportType,
    val reason: String?,
    val expiredAt: LocalDateTime,
)
