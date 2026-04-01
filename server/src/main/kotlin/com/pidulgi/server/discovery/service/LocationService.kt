package com.pidulgi.server.discovery.service

import com.pidulgi.server.common.dto.PageResponse
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LocationService(

    private val memberRepository: MemberRepository,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional(readOnly = true)
    fun getLocationMembers(
        memberId: Long,
        gender: String,
        page: Int,
        size: Int
    ): PageResponse<MemberDiscoveryResponse> {
        val result = memberRepository.findLocationMembersByPage(
            memberId,
            gender,
            page,
            size + 1
        ).map {
            MemberDiscoveryResponse(
                memberId = it.memberId,
                profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
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

        return PageResponse(
            payload = items,
            page = page,
            hasNext = hasNext
        )
    }
}