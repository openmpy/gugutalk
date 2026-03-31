package com.pidulgi.server.common.s3

data class PresignedUrlsResponse(

    val presigned: List<PresignedUrlResponse>
) {

    data class PresignedUrlResponse(

        val url: String,
        val key: String,
    )
}
