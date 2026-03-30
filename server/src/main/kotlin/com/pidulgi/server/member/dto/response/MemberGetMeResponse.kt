package com.pidulgi.server.member.dto.response

import com.pidulgi.server.member.entity.type.Gender

data class MemberGetMeResponse(

    val memberId: Long,
    val images: List<MemberImageResponse> = emptyList(),
    val nickname: String,
    val gender: Gender,
    val age: Int,
    val bio: String? = null,
    val likes: Int,
)
