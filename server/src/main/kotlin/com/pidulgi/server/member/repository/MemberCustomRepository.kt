package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.dto.MemberItemResponse
import java.time.LocalDateTime

interface MemberCustomRepository {

    fun findMembersByCursor(
        memberId: Long,
        gender: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MemberItemResponse>
}