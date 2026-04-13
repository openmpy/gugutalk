package com.pidulgi.server.report.repository.result

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class ReportAdminItemResult(

    val reportId: Long,
    val type: ReportType,
    val reporterNickname: String,
    val reportedNickname: String,
    val reason: String?,
    val hasImage: Boolean,
    val createdAt: LocalDateTime,
)
