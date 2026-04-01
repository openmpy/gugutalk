package com.pidulgi.server.member.dto.response

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class MemberGetResponse(

    val memberId: Long,
    val images: List<MemberImageResponse> = emptyList(),
    val nickname: String,
    val gender: Gender,
    val age: Int,
    val bio: String? = null,
    val likes: Long,
    val distance: Double? = null,
    val updatedAt: LocalDateTime,
    val isLiked: Boolean,
    val isBlocked: Boolean,
    val isPrivateImageGranted: Boolean,
)
