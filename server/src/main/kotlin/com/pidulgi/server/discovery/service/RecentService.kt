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

    private val memberRepository: MemberRepository,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

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

        return CursorResponse(
            payload = items,
            nextId = if (hasNext) items.last().memberId else null,
            nextDateAt = if (hasNext) items.last().updatedAt else null,
            hasNext = hasNext
        )
    }
}