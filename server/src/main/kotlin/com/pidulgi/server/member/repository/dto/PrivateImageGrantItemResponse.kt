package com.pidulgi.server.member.repository.dto

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class PrivateImageGrantItemResponse(

    val grantId: Long,
    val memberId: Long,
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val profileKey: String?,
    val createdAt: LocalDateTime,
)
