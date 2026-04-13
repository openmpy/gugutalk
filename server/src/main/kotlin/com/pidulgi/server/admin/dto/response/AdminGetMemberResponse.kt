package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class AdminGetMemberResponse(

    val memberId: Long,
    val uuid: String,
    val phoneNumber: String,
    val profileUrl: String?,
    val nickname: String,
    val gender: Gender,
    val age: Int,
    val comment: String?,
    val updatedAt: LocalDateTime,
)
