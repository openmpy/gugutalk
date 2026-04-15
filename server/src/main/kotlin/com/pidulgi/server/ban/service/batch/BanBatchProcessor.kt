package com.pidulgi.server.ban.service.batch

import com.pidulgi.server.ban.entity.Ban
import com.pidulgi.server.ban.entity.BanHistory
import com.pidulgi.server.ban.repository.BanHistoryRepository
import com.pidulgi.server.ban.repository.BanRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BanBatchProcessor(

    private val banRepository: BanRepository,
    private val banHistoryRepository: BanHistoryRepository,
) {

    @Transactional
    fun processBanBatch(bans: List<Ban>) {
        val banIds = bans.map { it.id }

        banRepository.deleteAllByIdInBatch(banIds)
    }

    @Transactional
    fun processBanHistoryBatch(banHistory: List<BanHistory>) {
        val banHistoryIds = banHistory.map { it.id }

        banHistoryRepository.deleteAllByIdInBatch(banHistoryIds)
    }
}