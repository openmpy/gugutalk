package com.pidulgi.server.ban.service

import com.pidulgi.server.ban.repository.BanHistoryRepository
import com.pidulgi.server.ban.repository.BanRepository
import com.pidulgi.server.ban.service.batch.BanBatchProcessor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BanScheduler(

    private val banRepository: BanRepository,
    private val banHistoryRepository: BanHistoryRepository,
    private val banBatchProcessor: BanBatchProcessor,
) {

    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpBans() {
        val expiredBefore = LocalDateTime.now().minusYears(1)
        val expiredBans = banRepository.findAllByCreatedAtBefore(expiredBefore)
        val expiredBanHistories = banHistoryRepository.findAllByCreatedAtBefore(expiredBefore)

        if (expiredBans.isNotEmpty()) {
            expiredBans.chunked(100).forEach { batch ->
                try {
                    banBatchProcessor.processBanBatch(batch)
                } catch (e: Exception) {
                    log.error(e) { "정지 삭제 배치 작업 실패 - ${batch.map { it.id }}" }
                }
            }
        }

        if (expiredBanHistories.isNotEmpty()) {
            expiredBanHistories.chunked(100).forEach { batch ->
                try {
                    banBatchProcessor.processBanHistoryBatch(batch)
                } catch (e: Exception) {
                    log.error(e) { "정지 기록 삭제 배치 작업 실패 - ${batch.map { it.id }}" }
                }
            }
        }
    }
}