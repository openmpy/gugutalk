package com.pidulgi.server.report.dto

data class ReportGetPresignedUrlsRequest(

    val images: List<ReportGetPresignedUrlRequest>
) {

    data class ReportGetPresignedUrlRequest(

        val contentType: String,
    )
}
