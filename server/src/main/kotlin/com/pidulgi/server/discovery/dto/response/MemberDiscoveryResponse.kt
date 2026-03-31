package com.pidulgi.server.discovery.dto.response

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class MemberDiscoveryResponse(

    val memberId: Long,
    val profileUrl: String?,
    val nickname: String,
    val gender: Gender,
    val age: Int,
    val comment: String?,
    val distance: Double?,
    val likes: Int,
    val updatedAt: LocalDateTime,
)
