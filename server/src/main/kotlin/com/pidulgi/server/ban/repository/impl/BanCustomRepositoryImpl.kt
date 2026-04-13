package com.pidulgi.server.ban.repository.impl

import com.pidulgi.server.ban.repository.BanCustomRepository
import com.pidulgi.server.ban.repository.result.BanAdminItemResult
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.report.entity.type.ReportType
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BanCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : BanCustomRepository {

    override fun findAllBansForAdminByCursor(
        type: String,
        keyword: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<BanAdminItemResult> {
        val normalizedType = type.uppercase()

        val keywordCondition = when (normalizedType) {
            "UUID" -> "AND b.uuid ILIKE :keyword"
            "PHONE" -> "AND b.phone_number ILIKE :keyword"
            else -> throw CustomException("지원하지 않는 검색 유형입니다. ($type)")
        }

        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                b.created_at < :cursorDate OR (b.created_at = :cursorDate AND b.id < :cursorId)
            )
            """.trimIndent()
        } else ""

        val sql = """
            SELECT b.id,
                b.type,
                b.uuid,
                b.reason,
                b.created_at,
                b.expired_at
            FROM ban b
            WHERE 1 = 1
                $keywordCondition
                $cursorCondition
            ORDER BY b.created_at DESC, b.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("size", size)
            setParameter("keyword", "%$keyword%")

            if (cursorId != null && cursorDate != null) {
                setParameter("cursorId", cursorId)
                setParameter("cursorDate", cursorDate)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toBanAdminItemResult)
    }

    private fun toBanAdminItemResult(row: Array<Any?>): BanAdminItemResult {
        return BanAdminItemResult(
            banId = (row[0] as Number).toLong(),
            type = ReportType.valueOf(row[1] as String),
            uuid = row[2] as String,
            reason = row[3] as? String,
            createdAt = row[4] as LocalDateTime,
            expiredAt = row[5] as LocalDateTime,
        )
    }
}