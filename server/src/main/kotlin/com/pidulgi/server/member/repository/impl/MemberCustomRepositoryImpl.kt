package com.pidulgi.server.member.repository.impl

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.repository.MemberCustomRepository
import com.pidulgi.server.member.repository.dto.MemberItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class MemberCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : MemberCustomRepository {

    override fun findMembersByCursor(
        memberId: Long,
        gender: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MemberItemResponse> {
        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                m.updated_at < :cursorDate
                OR (m.updated_at = :cursorDate AND m.id < :cursorId)
            )
            """.trimIndent()
        } else ""
        val sql = """
            SELECT m.id, m.nickname, m.gender, m.birth_year, m.bio, m.comment, m.profile_key, m.updated_at,
                   CASE WHEN m.location IS NOT NULL AND req.location IS NOT NULL
                        THEN ST_Distance(m.location, req.location) / 1000.0
                   END AS distance,
                   (SELECT COUNT(*) FROM likes l WHERE l.liked_id = m.id) AS likes
            FROM member m
            CROSS JOIN (SELECT location FROM member WHERE id = :requesterId) req
            WHERE m.deleted_at IS NULL
              AND m.id != :requesterId
              AND (:gender = 'ALL' OR m.gender = :gender)
              $cursorCondition
            ORDER BY m.updated_at DESC, m.id DESC
            LIMIT :size
        """.trimIndent()
        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("requesterId", memberId)
            setParameter("gender", gender)
            setParameter("size", size)

            if (cursorId != null && cursorDate != null) {
                setParameter("cursorDate", cursorDate)
                setParameter("cursorId", cursorId)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberItemResponse)
    }

    private fun toMemberItemResponse(row: Array<Any?>) = MemberItemResponse(
        memberId = (row[0] as Number).toLong(),
        nickname = row[1] as String,
        gender = Gender.valueOf(row[2] as String),
        birthYear = (row[3] as Number).toInt(),
        bio = row[4] as String?,
        comment = row[5] as String?,
        profileKey = row[6] as String?,
        updatedAt = row[7] as LocalDateTime,
        distance = (row[8] as? Number)?.toDouble(),
        likes = (row[9] as Number).toInt(),
    )
}