package com.pidulgi.server.common.dto

import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class SettingResponse(

    val id: Long,
    val memberId: Long,
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val profileUrl: String?,
    val createdAt: LocalDateTime,
)