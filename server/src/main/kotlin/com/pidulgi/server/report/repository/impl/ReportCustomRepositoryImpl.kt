package com.pidulgi.server.report.repository.impl

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.report.entity.type.ReportType
import com.pidulgi.server.report.repository.ReportCustomRepository
import com.pidulgi.server.report.repository.result.ReportAdminItemResult
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ReportCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : ReportCustomRepository {

    override fun findAllReportsForAdminByCursor(
        type: String,
        keyword: String,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ReportAdminItemResult> {
        val normalizedType = type.uppercase()

        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                r.created_at < :cursorDate OR (r.created_at = :cursorDate AND r.id < :cursorId)
            )
            """.trimIndent()
        } else ""

        val statusCondition = when (status.uppercase()) {
            "PENDING", "RESOLVE", "REJECT" -> "AND r.status = :status"
            else -> ""
        }

        val keywordCondition = when (normalizedType) {
            "UUID" -> "AND r.reporter_uuid ILIKE :keyword"
            "PHONE" -> "AND r.reporter_phone_number ILIKE :keyword"
            "NICKNAME" -> "AND r.reporter_nickname ILIKE :keyword"
            else -> throw CustomException("지원하지 않는 검색 유형입니다. ($type)")
        }

        val sql = """
            SELECT r.id,
                   r.type,
                   r.reporter_nickname,
                   r.reported_nickname,
                   r.reason,
                   EXISTS (
                       SELECT 1
                       FROM report_image ri
                       WHERE ri.report_id = r.id
                         AND ri.status = 'COMPLETE'
                   ) AS has_image,
                   r.created_at
            FROM report r
            WHERE 1 = 1
                $statusCondition
                $keywordCondition
                $cursorCondition
            ORDER BY r.created_at DESC, r.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("size", size)
            setParameter("keyword", "%$keyword%")

            if (status.uppercase() in listOf("PENDING", "RESOLVE", "REJECT")) {
                setParameter("status", status.uppercase())
            }
            if (cursorId != null && cursorDate != null) {
                setParameter("cursorId", cursorId)
                setParameter("cursorDate", cursorDate)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toReportAdminItemResult)
    }

    private fun toReportAdminItemResult(row: Array<Any?>): ReportAdminItemResult {
        return ReportAdminItemResult(
            reportId = (row[0] as Number).toLong(),
            type = ReportType.valueOf(row[1] as String),
            reporterNickname = row[2] as String,
            reportedNickname = row[3] as String,
            reason = row[4] as? String,
            hasImage = row[5] as Boolean,
            createdAt = row[6] as LocalDateTime,
        )
    }
}