package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class AdminGetReportResponse(

    val reportId: Long,
    val type: ReportType,
    val reporterNickname: String,
    val reportedNickname: String,
    val reason: String?,
    val createdAt: LocalDateTime,
)
