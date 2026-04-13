package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.service.extension.toAdminGetMemberResponse
import com.pidulgi.server.admin.service.query.AdminGetMembersQuery
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getMembers(query: AdminGetMembersQuery): CursorResponse<AdminGetMemberResponse> {
        val result = memberRepository.findAllMembersForAdminByCursor(
            query.type,
            query.keyword,
            query.gender,
            query.cursorId,
            query.cursorDate,
            query.size + 1
        ).map { it.toAdminGetMemberResponse(endpoint) }

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