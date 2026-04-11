package com.pidulgi.server.discovery.service

import com.pidulgi.server.common.dto.CursorDistanceResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.discovery.service.extension.toMemberDiscoveryResponse
import com.pidulgi.server.discovery.service.query.GetLocationMembersQuery
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getLocationMembers(query: GetLocationMembersQuery): CursorDistanceResponse<MemberDiscoveryResponse> {
        val member = (memberRepository.findByIdOrNull(query.memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))
        val location = member.location
            ?: throw CustomException("위치 정보를 불러올 수 없습니다.")

        val result = memberRepository.findAllMembersWithDistanceByCursor(
            query.memberId,
            location,
            query.gender,
            query.cursorId,
            query.cursorDistance?.times(1000),
            query.size + 1
        ).map { it.toMemberDiscoveryResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorDistanceResponse(
            payload = items,
            nextId = items.lastOrNull()?.memberId,
            nextDistance = items.lastOrNull()?.distance,
            hasNext = hasNext
        )
    }
}