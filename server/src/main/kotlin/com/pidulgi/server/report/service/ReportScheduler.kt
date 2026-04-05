package com.pidulgi.server.report.service

import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.report.entity.type.ReportImageStatus.PENDING
import com.pidulgi.server.report.repository.ReportImageRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReportScheduler(

    private val reportImageRepository: ReportImageRepository,
    private val s3Service: S3Service,
) {

    @Scheduled(cron = "0 0 8 * * *")
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
}