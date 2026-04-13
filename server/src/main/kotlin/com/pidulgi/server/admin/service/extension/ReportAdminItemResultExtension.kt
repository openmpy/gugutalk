package com.pidulgi.server.admin.service.extension

import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.report.repository.result.ReportAdminItemResult

fun ReportAdminItemResult.toAdminGetReportResponse() = AdminGetReportResponse(

    reportId = this.reportId,
    type = this.type,
    reporterNickname = this.reporterNickname,
    reportedNickname = this.reportedNickname,
    reason = this.reason,
    hasImage = this.hasImage,
    createdAt = this.createdAt,
)