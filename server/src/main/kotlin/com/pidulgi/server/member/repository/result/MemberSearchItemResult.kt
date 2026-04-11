package com.pidulgi.server.member.repository.result

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.vo.MemberBirthYear
import com.pidulgi.server.member.entity.vo.MemberComment
import com.pidulgi.server.member.entity.vo.MemberNickname
import java.time.LocalDateTime

data class MemberSearchItemResult(

    val memberId: Long,
    val profileKey: String?,
    val nickname: MemberNickname,
    val gender: Gender,
    val birthYear: MemberBirthYear,
    val comment: MemberComment,
    val updatedAt: LocalDateTime,
    val distance: Double?,
    val likes: Int,
    val similarityScore: Double,
)
