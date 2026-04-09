package com.pidulgi.server.discovery.service

import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class RecentService(
    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getRecentMembers(
        memberId: Long,
        gender: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int = 20
    ): CursorResponse<MemberDiscoveryResponse> {
        val result = memberRepository.findMembersByCursor(
            memberId,
            gender,
            cursorId,
            cursorDate,
            size + 1
        ).map {
            MemberDiscoveryResponse(
                memberId = it.memberId,
                profileUrl = it.profileKey?.let { "$endpoint$it" },
                nickname = it.nickname,
                gender = it.gender,
                age = LocalDate.now().year - it.birthYear,
                comment = it.comment,
                distance = it.distance,
                likes = it.likes,
                updatedAt = it.updatedAt,
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result
        val last = items.lastOrNull()

        return CursorResponse(
            payload = items,
            nextId = last?.memberId,
            nextDateAt = last?.updatedAt,
            hasNext = hasNext
        )
    }
}