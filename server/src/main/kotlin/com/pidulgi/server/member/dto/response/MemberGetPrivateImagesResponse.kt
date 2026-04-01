package com.pidulgi.server.member.dto.response

data class MemberGetPrivateImagesResponse(

    val images: List<MemberPrivateImageResponse>,
) {

    data class MemberPrivateImageResponse(

        val url: String,
    )
}
