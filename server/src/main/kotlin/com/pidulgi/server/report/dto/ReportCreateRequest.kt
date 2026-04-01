package com.pidulgi.server.report.dto

import com.pidulgi.server.report.entity.type.ReportType

data class ReportCreateRequest(

    val images: List<ReportImageRequest> = emptyList(),
    val type: ReportType,
    val reason: String?,
) {

    data class ReportImageRequest(

        val index: Int,
        val key: String,
    )
}
