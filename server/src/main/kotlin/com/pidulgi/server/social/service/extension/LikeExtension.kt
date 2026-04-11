package com.pidulgi.server.social.service.extension

import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.social.repository.result.LikeItemResult

fun LikeItemResult.toSettingResponse(endpoint: String) = SettingResponse(

    id = this.likeId,
    memberId = this.memberId,
    nickname = this.nickname.value,
    gender = this.gender,
    age = AgeCalculator.calculate(this.birthYear.value),
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    createdAt = this.createdAt,
)