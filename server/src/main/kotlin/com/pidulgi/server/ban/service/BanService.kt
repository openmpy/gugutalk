package com.pidulgi.server.ban.service

import com.pidulgi.server.ban.dto.request.BanAddRequest
import com.pidulgi.server.ban.entity.Ban
import com.pidulgi.server.ban.entity.BanHistory
import com.pidulgi.server.ban.repository.BanHistoryRepository
import com.pidulgi.server.ban.repository.BanRepository
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.report.entity.type.ReportType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BanService(

    private val banRepository: BanRepository,
    private val banHistoryRepository: BanHistoryRepository,
) {

    @Transactional
    fun add(request: BanAddRequest) {
        if (banRepository.existsByUuid(request.uuid)) {
            throw CustomException("이미 정지 당한 계정입니다.")
        }

        val type = ReportType.from(request.type)

        val ban = Ban(
            uuid = request.uuid,
            phoneNumber = request.phoneNumber,
            type = type,
            reason = request.reason,
            expiredAt = request.expiredAt
        )
        banRepository.save(ban)

        val banHistory = BanHistory(
            uuid = request.uuid,
            phoneNumber = request.phoneNumber,
            type = type,
            reason = request.reason,
            expiredAt = request.expiredAt
        )
        banHistoryRepository.save(banHistory)
    }
}