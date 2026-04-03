package com.pidulgi.server.member.dto.request

import com.pidulgi.server.member.entity.type.ImageType

data class MemberGetPresignedUrlsRequest(

    val images: List<MemberGetPresignedUrlRequest>
) {

    data class MemberGetPresignedUrlRequest(

        val imageType: ImageType,
        val contentType: String,
    )
}
