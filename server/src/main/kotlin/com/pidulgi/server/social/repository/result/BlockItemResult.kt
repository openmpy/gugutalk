package com.pidulgi.server.social.repository.result

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.vo.MemberBirthYear
import com.pidulgi.server.member.entity.vo.MemberNickname
import java.time.LocalDateTime

data class BlockItemResult(

    val blockId: Long,
    val memberId: Long,
    val nickname: MemberNickname,
    val gender: Gender,
    val birthYear: MemberBirthYear,
    val profileKey: String?,
    val createdAt: LocalDateTime,
)
