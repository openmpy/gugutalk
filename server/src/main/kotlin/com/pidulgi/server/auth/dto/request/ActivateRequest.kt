package com.pidulgi.server.auth.dto.request

data class ActivateRequest(

    val images: List<ActivateImageRequest> = emptyList(),
    val nickname: String,
    val birthYear: Int,
    val bio: String?,
) {

    data class ActivateImageRequest(

        val index: Int,
        val key: String,
    )
}