package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.report.entity.type.ReportType
import java.time.LocalDateTime

data class AdminGetReportDetailResponse(

    val reportId: Long,
    val type: ReportType,
    val reporterId: Long,
    val reporterUuid: String,
    val reporterPhone: String,
    val reporterNickname: String,
    val reportedId: Long,
    val reportedUuid: String,
    val reportedPhone: String,
    val reportedNickname: String,
    val reason: String?,
    val createdAt: LocalDateTime,
    val images: List<AdminGetReportImageResponse>,
) {

    data class AdminGetReportImageResponse(

        val imageId: Long,
        val url: String,
        val key: String,
        val sortOrder: Int,
        val createdAt: LocalDateTime,
    )
}
