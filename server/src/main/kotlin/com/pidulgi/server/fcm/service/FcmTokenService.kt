package com.pidulgi.server.fcm.service

import com.pidulgi.server.fcm.dto.request.FcmTokenRegisterRequest
import com.pidulgi.server.fcm.entity.FcmToken
import com.pidulgi.server.fcm.repository.FcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(

    private val fcmTokenRepository: FcmTokenRepository,
) {

    @Transactional
    fun register(request: FcmTokenRegisterRequest) {
        val fcmToken = fcmTokenRepository.findByUuid(request.uuid)

        if (fcmToken == null) {
            fcmTokenRepository.save(
                FcmToken(
                    token = request.token,
                    uuid = request.uuid,
                    memberId = request.memberId,
                )
            )
            return
        }

        fcmToken.update(request.uuid, request.memberId)
    }

    @Transactional
    fun inactive(uuid: String) {
        fcmTokenRepository.findByUuid(uuid)?.inactive()
    }
}