package com.pidulgi.server.chat.dto.request

data class MediaGetPresignedUrlsRequest(

    val medias: List<MediaGetPresignedUrlRequest>
) {

    data class MediaGetPresignedUrlRequest(

        val contentType: String,
    )
}
