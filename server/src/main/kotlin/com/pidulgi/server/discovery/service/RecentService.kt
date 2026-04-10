package com.pidulgi.server.discovery.service

import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.discovery.service.extension.toMemberDiscoveryResponse
import com.pidulgi.server.discovery.service.query.GetRecentMembersQuery
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecentService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getRecentMembers(query: GetRecentMembersQuery): CursorResponse<MemberDiscoveryResponse> {
        val member = (memberRepository.findByIdOrNull(query.memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        val result = memberRepository.findAllMembersByCursor(
            query.memberId,
            member.location,
            query.gender,
            query.cursorId,
            query.cursorDate,
            query.size + 1
        ).map { it.toMemberDiscoveryResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.memberId,
            nextDateAt = items.lastOrNull()?.updatedAt,
            hasNext = hasNext
        )
    }
}