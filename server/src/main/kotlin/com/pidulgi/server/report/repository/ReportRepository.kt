package com.pidulgi.server.report.repository

import com.pidulgi.server.report.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ReportRepository : JpaRepository<Report, Long> {

    @Query(
        value = """
            SELECT *
            FROM report r
            WHERE r.created_at <= :deletedAt
        """,
        nativeQuery = true
    )
    fun findAllDeleted(@Param("deletedAt") deletedAt: LocalDateTime): List<Report>
}