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

    fun findLocationMembersByPage(
        memberId: Long,
        gender: String,
        page: Int,
        size: Int
    ): List<MemberItemResponse>

    fun searchByNickname(
        memberId: Long,
        keyword: String,
        cursorId: Long?,
        size: Int
    ): List<MemberItemResponse>

    fun getDistanceBetween(fromId: Long, toId: Long): Double?
}