package com.pidulgi.server.admin.dto.request

import com.pidulgi.server.report.entity.type.ReportType

data class BanAddRequest(

    val uuid: String,
    val nickname: String?,
    val phoneNumber: String,
    val day: Long,
    val type: ReportType,
    val reason: String?,
)
