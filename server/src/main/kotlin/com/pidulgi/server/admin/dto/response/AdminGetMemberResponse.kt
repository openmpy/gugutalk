package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class AdminGetMemberResponse(

    val memberId: Long,
    val profileUrl: String?,
    val nickname: String,
    val age: Int,
    val gender: Gender,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
)
