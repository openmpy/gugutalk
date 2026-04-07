package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminMemberResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
) {

    @Transactional(readOnly = true)
    fun getMembers(page: Int, size: Int): Page<AdminMemberResponse> {
        val sort = Sort.by(Sort.Direction.DESC, "updatedAt")
        val pageRequest = PageRequest.of(page, size, sort)
        return memberRepository.findAll(pageRequest).map { AdminMemberResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun get(memberId: Long): AdminGetMemberResponse {
        val member = (memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))
        val images = memberImageRepository.findAllByMemberIdOrderByTypeAscSortOrderAsc(
            member.id
        )
        return AdminGetMemberResponse.of(member, images)
    }
}