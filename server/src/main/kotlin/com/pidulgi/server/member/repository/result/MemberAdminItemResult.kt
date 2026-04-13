package com.pidulgi.server.member.repository.result

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.vo.*
import java.time.LocalDateTime

data class MemberAdminItemResult(

    val memberId: Long,
    val uuid: MemberUuid,
    val phoneNumber: MemberPhoneNumber,
    val profileKey: String?,
    val nickname: MemberNickname,
    val gender: Gender,
    val birthYear: MemberBirthYear,
    val comment: MemberComment,
    val updatedAt: LocalDateTime,
)
