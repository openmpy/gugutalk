package com.pidulgi.server.member.service.extension

import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.member.dto.response.MemberSearchResponse
import com.pidulgi.server.member.repository.result.MemberSearchItemResult

fun MemberSearchItemResult.toMemberDiscoveryResponse(endpoint: String) = MemberSearchResponse(

    memberId = this.memberId,
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    nickname = this.nickname.value,
    gender = this.gender,
    age = AgeCalculator.calculate(this.birthYear.value),
    comment = this.comment.value,
    distance = this.distance?.div(1000),
    likes = this.likes,
    updatedAt = this.updatedAt,
    similarityScore = this.similarityScore,
)