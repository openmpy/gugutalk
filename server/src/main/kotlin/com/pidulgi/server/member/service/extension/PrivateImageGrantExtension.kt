package com.pidulgi.server.member.service.extension

import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.member.repository.result.PrivateImageGrantItemResult

fun PrivateImageGrantItemResult.toSettingResponse(endpoint: String) = SettingResponse(

    id = this.grantId,
    memberId = this.memberId,
    nickname = this.nickname.value,
    gender = this.gender,
    age = AgeCalculator.calculate(this.birthYear.value),
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    createdAt = this.createdAt,
)