package com.pidulgi.server.report.dto.request

data class ReportCreateRequest(

    val images: List<ReportImageRequest> = emptyList(),
    val type: String,
    val reason: String?,
) {

    data class ReportImageRequest(

        val index: Int,
        val key: String,
    )
}
