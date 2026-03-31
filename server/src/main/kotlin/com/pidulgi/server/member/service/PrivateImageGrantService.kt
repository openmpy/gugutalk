package com.pidulgi.server.member.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrivateImageGrantService(

    private val privateImageGrantRepository: PrivateImageGrantRepository,
) {

    @Transactional
    fun open(granterId: Long, granteeId: Long) {
        if (granteeId != granterId) {
            throw CustomException("자기 자신에게 공개할 수 없습니다.")
        }
        if (privateImageGrantRepository.existsByGranterIdAndGranteeId(granterId, granteeId)) {
            throw CustomException("이미 공개한 대상입니다.")
        }

        val privateImageGrant = PrivateImageGrant(
            granterId = granterId,
            granteeId = granteeId,
        )
        privateImageGrantRepository.save(privateImageGrant)
    }

    @Transactional
    fun close(granterId: Long, granteeId: Long) {
        val privateImageGrant = (privateImageGrantRepository.findByGranterIdAndGranteeId(
            granterId, granteeId
        ) ?: throw CustomException("공개 한 적이 없습니다."))

        privateImageGrantRepository.delete(privateImageGrant)
    }
}