package com.pidulgi.server.admin.service.extension

import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.member.repository.result.MemberAdminItemResult

fun MemberAdminItemResult.toAdminGetMemberResponse(endpoint: String) = AdminGetMemberResponse(

    memberId = this.memberId,
    uuid = this.uuid.value,
    phoneNumber = this.phoneNumber.value,
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    nickname = this.nickname.value,
    gender = this.gender,
    age = AgeCalculator.calculate(this.birthYear.value),
    comment = this.comment.value,
    updatedAt = this.updatedAt
)