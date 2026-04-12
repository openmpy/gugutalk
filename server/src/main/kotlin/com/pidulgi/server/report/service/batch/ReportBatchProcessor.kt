package com.pidulgi.server.report.service.batch

import com.pidulgi.server.common.s3.event.S3CleanupEvent
import com.pidulgi.server.report.entity.Report
import com.pidulgi.server.report.repository.ReportImageRepository
import com.pidulgi.server.report.repository.ReportRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReportBatchProcessor(

    private val reportRepository: ReportRepository,
    private val reportImageRepository: ReportImageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun processBatch(reports: List<Report>) {
        val reportIds = reports.map { it.id }
        val reportImages = reportImageRepository.findAllByReportIdIn(reportIds)

        reportImageRepository.deleteAllInBatch(reportImages)
        reportRepository.deleteAllInBatch(reports)

        applicationEventPublisher.publishEvent(S3CleanupEvent(reportImages.map { it.key }))
    }
}