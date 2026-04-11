package com.pidulgi.server.discovery.service.extension

import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.member.repository.result.MemberItemResult

fun MemberItemResult.toMemberDiscoveryResponse(endpoint: String) = MemberDiscoveryResponse(

    memberId = this.memberId,
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    nickname = this.nickname.value,
    gender = this.gender,
    age = AgeCalculator.calculate(this.birthYear.value),
    comment = this.comment.value,
    distance = this.distance?.div(1000),
    likes = this.likes,
    updatedAt = this.updatedAt,
)