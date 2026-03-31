package com.pidulgi.server.member.dto.response

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class PrivateImageGrantResponse(

    val grantId: Long,
    val memberId: Long,
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val profileUrl: String?,
    val createdAt: LocalDateTime,
)
