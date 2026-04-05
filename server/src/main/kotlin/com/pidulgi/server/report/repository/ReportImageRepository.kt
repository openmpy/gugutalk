package com.pidulgi.server.report.repository

import com.pidulgi.server.report.entity.ReportImage
import com.pidulgi.server.report.entity.type.ReportImageStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ReportImageRepository : JpaRepository<ReportImage, Long> {

    fun findAllByKeyIn(keys: Collection<String>): List<ReportImage>

    fun findByStatusAndCreatedAtBefore(
        status: ReportImageStatus,
        createdAt: LocalDateTime
    ): List<ReportImage>
}