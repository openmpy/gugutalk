package com.pidulgi.server.report.repository

import com.pidulgi.server.report.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReportRepository : JpaRepository<Report, Long> {

    @Query(
        value = """
            SELECT *
            FROM report r
            WHERE r.status = :status
            ORDER BY r.created_at DESC, r.id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true
    )
    fun findAllByPage(
        @Param("status") status: String,
        @Param("offset") offset: Int,
        @Param("size") size: Int
    ): List<Report>
}