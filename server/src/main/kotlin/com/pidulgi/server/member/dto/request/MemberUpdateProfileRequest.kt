package com.pidulgi.server.member.dto.request

data class MemberUpdateProfileRequest(

    val images: List<ProfileImageUpdate> = emptyList(),
    val nickname: String,
    val birthYear: Int,
    val bio: String? = null,
) {

    data class ProfileImageUpdate(

        val imageId: Long? = null,
        val key: String? = null,
        val sortOrder: Int,
    )
}
