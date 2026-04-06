package com.pidulgi.server.fcm.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.fcm.dto.request.FcmTokenRegisterRequest
import com.pidulgi.server.fcm.entity.FcmToken
import com.pidulgi.server.fcm.repository.FcmTokenRepository
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(

    private val fcmTokenRepository: FcmTokenRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun register(memberId: Long, request: FcmTokenRegisterRequest) {
        (memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        if (request.uuid != null) {
            fcmTokenRepository.findByMemberIdAndUuid(memberId, request.uuid)
                ?.let {
                    it.update(request.token)
                    return
                }
        }

        fcmTokenRepository.findByToken(request.token)?.let {
            it.isActive = false
        }

        val fcmToken = FcmToken(
            memberId = memberId,
            token = request.token,
        )
        fcmTokenRepository.save(fcmToken)
    }
}