package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.report.dto.response.ReportImageResponse
import com.pidulgi.server.report.entity.type.ReportStatus
import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class AdminGetReportDetailResponse(

    val reportId: Long,
    val reporterId: Long,
    val reporterUuid: String,
    val reporterPhoneNumber: String,
    val reporterNickname: String,
    val reportedId: Long,
    val reportedUuid: String,
    val reportedPhoneNumber: String,
    val reportedNickname: String,
    val type: ReportType,
    val reason: String?,
    val status: ReportStatus,
    val createdAt: LocalDateTime,
    val images: List<ReportImageResponse>
)
