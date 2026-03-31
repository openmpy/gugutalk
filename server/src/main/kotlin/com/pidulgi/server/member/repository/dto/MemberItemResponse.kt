package com.pidulgi.server.member.repository.dto

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class MemberItemResponse(

    val memberId: Long,
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val bio: String?,
    val comment: String?,
    val profileKey: String?,
    val updatedAt: LocalDateTime,
    val distance: Double?,
    val likes: Int,
)
