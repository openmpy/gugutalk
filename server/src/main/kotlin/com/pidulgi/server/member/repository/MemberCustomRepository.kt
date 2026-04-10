package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.dto.MemberItemResponse
import com.pidulgi.server.member.repository.dto.MemberItemResult
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

interface MemberCustomRepository {

    fun findAllMembersByCursor(
        memberId: Long,
        location: Point?,
        gender: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MemberItemResult>

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