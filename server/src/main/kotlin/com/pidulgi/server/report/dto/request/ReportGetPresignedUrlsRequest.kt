package com.pidulgi.server.report.dto.request

data class ReportGetPresignedUrlsRequest(

    val images: List<ReportGetPresignedUrlRequest>
) {

    data class ReportGetPresignedUrlRequest(

        val contentType: String,
    )
}
