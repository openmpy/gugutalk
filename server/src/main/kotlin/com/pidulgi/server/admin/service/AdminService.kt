package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.common.dto.PageResponse
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AdminService(
    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getMembers(
        page: Int,
        size: Int
    ): PageResponse<AdminGetMemberResponse> {
        val offset = page * size
        val result = memberRepository.findAllByPage(offset, size + 1)
            .map {
                AdminGetMemberResponse(
                    memberId = it.id,
                    profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                    nickname = it.nickname,
                    age = LocalDate.now().year - it.birthYear,
                    gender = it.gender,
                    updatedAt = it.updatedAt,
                    deletedAt = it.deletedAt
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