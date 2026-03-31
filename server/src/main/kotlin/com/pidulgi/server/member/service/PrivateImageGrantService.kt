package com.pidulgi.server.member.service

import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.dto.response.PrivateImageGrantResponse
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PrivateImageGrantService(

    private val privateImageGrantRepository: PrivateImageGrantRepository,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

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

    @Transactional(readOnly = true)
    fun getGrantedMembers(
        granterId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int = 20
    ): CursorResponse<PrivateImageGrantResponse> {
        val result = privateImageGrantRepository.findGrantsByCursor(
            granterId,
            cursorId,
            cursorDate,
            size + 1
        ).map {
            PrivateImageGrantResponse(
                grantId = it.grantId,
                memberId = it.memberId,
                nickname = it.nickname,
                gender = it.gender,
                birthYear = it.birthYear,
                profileUrl = it.profileKey?.let { "$endpoint$it" },
                createdAt = it.createdAt,
            )
        }
        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = if (hasNext) items.last().grantId else null,
            nextDateAt = if (hasNext) items.last().createdAt else null,
            hasNext = hasNext
        )
    }
}