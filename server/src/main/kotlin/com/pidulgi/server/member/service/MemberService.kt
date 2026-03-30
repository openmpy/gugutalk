package com.pidulgi.server.member.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MemberService(

    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getMe(memberId: Long): MemberGetMeResponse {
        val member = (memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        return MemberGetMeResponse(
            member.id,
            emptyList(),
            member.nickname,
            member.gender,
            LocalDate.now().year - member.birthYear,
            member.bio,
            0
        )
    }
}