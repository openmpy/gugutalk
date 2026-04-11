package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.dto.MemberItemResult
import com.pidulgi.server.member.repository.dto.MemberSearchItemResult
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

    fun findAllMembersWithDistanceByCursor(
        memberId: Long,
        location: Point,
        gender: String,
        cursorId: Long?,
        cursorDistance: Double?,
        size: Int
    ): List<MemberItemResult>

    fun findAllMembersByNicknameWithCursor(
        memberId: Long,
        nickname: String,
        location: Point?,
        cursorId: Long?,
        cursorSimilarity: Double?,
        size: Int
    ): List<MemberSearchItemResult>

    fun findDistanceFromLocation(
        location: Point?,
        memberId: Long
    ): Double?
}