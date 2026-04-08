package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class BanGetResponse(

    val banId: Long,
    val type: ReportType,
    val uuid: String,
    val nickname: String?,
    val reason: String?,
    val expiredAt: LocalDateTime,
)
