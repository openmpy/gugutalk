package com.pidulgi.server.member.dto.response

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class MemberSearchResponse(

    val memberId: Long,
    val profileUrl: String?,
    val nickname: String,
    val gender: Gender,
    val age: Int,
    val comment: String?,
    val distance: Double?,
    val likes: Int,
    val updatedAt: LocalDateTime,
    val similarityScore: Double,
)
