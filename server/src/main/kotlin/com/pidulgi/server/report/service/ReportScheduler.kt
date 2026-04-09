package com.pidulgi.server.report.service

import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.report.entity.type.ReportImageStatus.PENDING
import com.pidulgi.server.report.repository.ReportImageRepository
import com.pidulgi.server.report.repository.ReportRepository
import com.pidulgi.server.report.service.batch.ReportBatchProcessor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReportScheduler(

    private val reportRepository: ReportRepository,
    private val reportImageRepository: ReportImageRepository,
    private val reportBatchProcessor: ReportBatchProcessor,
    private val s3Service: S3Service,
) {

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpPendingImages() {
        val expiredBefore = LocalDateTime.now().minusHours(24)
        val pendingImages = reportImageRepository.findByStatusAndCreatedAtBefore(
            PENDING, expiredBefore
        )

        if (pendingImages.isEmpty()) {
            return
        }

        reportImageRepository.deleteAllByIdInBatch(pendingImages.map { it.id })
        s3Service.deleteAll(pendingImages.map { it.key })
    }

    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpReports() {
        val expiredBefore = LocalDateTime.now().minusYears(1)
        val deletedReports = reportRepository.findAllDeleted(expiredBefore)

        if (deletedReports.isEmpty()) {
            return
        }

        deletedReports.chunked(100) { batch ->
            try {
                reportBatchProcessor.processBatch(batch)
            } catch (e: Exception) {
                log.error(e) { "신고 삭제 배치 작업 실패 - ${batch.map { it.id }}" }
            }
        }
    }
}