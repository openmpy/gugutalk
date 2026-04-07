package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.request.BanAddRequest
import com.pidulgi.server.admin.dto.request.BanUpdateRequest
import com.pidulgi.server.admin.entity.Ban
import com.pidulgi.server.admin.repository.BanRepository
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BanService(

    private val banRepository: BanRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun add(memberId: Long, request: BanAddRequest) {
        val member = (memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        val ban = Ban(
            memberId = memberId,
            uuid = member.uuid,
            nickname = member.nickname,
            phoneNumber = member.phoneNumber,
            type = request.type,
            reason = request.reason,
            expiredAt = request.expiredAt,
        )
        banRepository.save(ban)
    }

    @Transactional
    fun update(banId: Long, request: BanUpdateRequest) {
        val ban = (banRepository.findByIdOrNull(banId)
            ?: throw CustomException("존재하지 않는 정지 정보입니다."))

        ban.update(request.type, request.reason, request.expiredAt)
    }

    @Transactional
    fun remove(banId: Long) {
        val ban = (banRepository.findByIdOrNull(banId)
            ?: throw CustomException("존재하지 않는 정지 정보입니다."))

        banRepository.delete(ban)
    }

    @Transactional(readOnly = true)
    fun gets(page: Int, size: Int): Page<Ban> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageRequest = PageRequest.of(page, size, sort)
        return banRepository.findAll(pageRequest)
    }
}