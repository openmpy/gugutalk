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

    @Query(
        value = """
            SELECT *
            FROM report r
            WHERE r.reporter_nickname ILIKE '%' || :keyword || '%'
                AND r.status = :status
            ORDER BY r.created_at DESC, r.id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true
    )
    fun findAllByReporterNicknamePage(
        @Param("keyword") keyword: String,
        @Param("status") status: String,
        @Param("offset") offset: Int,
        @Param("size") size: Int
    ): List<Report>

    @Query(
        value = """
            SELECT *
            FROM report r
            WHERE r.reported_nickname ILIKE '%' || :keyword || '%'
                AND r.status = :status
            ORDER BY r.created_at DESC, r.id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true
    )
    fun findAllByReportedNicknamePage(
        @Param("keyword") keyword: String,
        @Param("status") status: String,
        @Param("offset") offset: Int,
        @Param("size") size: Int
    ): List<Report>

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