package com.pidulgi.server.member.repository.impl

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.vo.MemberBirthYear
import com.pidulgi.server.member.entity.vo.MemberComment
import com.pidulgi.server.member.entity.vo.MemberNickname
import com.pidulgi.server.member.repository.MemberCustomRepository
import com.pidulgi.server.member.repository.dto.MemberItemResponse
import com.pidulgi.server.member.repository.dto.MemberItemResult
import jakarta.persistence.EntityManager
import org.locationtech.jts.geom.Point
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MemberCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : MemberCustomRepository {

    override fun findAllMembersByCursor(
        memberId: Long,
        location: Point?,
        gender: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MemberItemResult> {
        val cursorCondition = if (cursorId != null && cursorDate != null) {
            """
            AND (
                m.updated_at < :cursorDate OR (m.updated_at = :cursorDate AND m.id < :cursorId)
            )
            """.trimIndent()
        } else ""

        val distanceExpression = if (location != null) {
            "ST_Distance(m.location, :location)"
        } else {
            "NULL"
        }

        val genderCondition = if (gender == "MALE" || gender == "FEMALE") {
            "AND m.gender = :gender"
        } else ""

        val sql = """
            SELECT m.id,
                   m.profile_key,
                   m.nickname,
                   m.gender,
                   m.birth_year,
                   m.comment,
                   m.updated_at,
                   $distanceExpression AS distance,
                   (SELECT COUNT(*) FROM likes l WHERE l.liked_id = m.id) AS likes
            FROM member m
            WHERE m.deleted_at IS NULL
                AND m.id <> :memberId
                AND m.id NOT IN (
                    SELECT b.blocked_id FROM blocks b WHERE b.blocker_id = :memberId
                    UNION
                    SELECT b.blocker_id FROM blocks b WHERE b.blocked_id = :memberId
                )
                $genderCondition
                $cursorCondition
            ORDER BY m.updated_at DESC, m.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("memberId", memberId)
            setParameter("size", size)

            if (location != null) {
                setParameter("location", location)
            }
            if (gender == "MALE" || gender == "FEMALE") {
                setParameter("gender", gender)
            }
            if (cursorId != null && cursorDate != null) {
                setParameter("cursorDate", cursorDate)
                setParameter("cursorId", cursorId)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberItemResult)
    }

    override fun findAllMembersWithDistanceByCursor(
        memberId: Long,
        location: Point,
        gender: String,
        cursorId: Long?,
        cursorDistance: Double?,
        size: Int
    ): List<MemberItemResult> {
        val cursorCondition = if (cursorId != null && cursorDistance != null) {
            """
            AND (
                (m.location <-> :location) > :cursorDistance
                OR (
                    ABS((m.location <-> :location) - :cursorDistance) < 1e-9
                    AND m.id < :cursorId
                )
            )
            """.trimIndent()
        } else ""

        val genderCondition = if (gender == "MALE" || gender == "FEMALE") {
            "AND m.gender = :gender"
        } else ""

        val sql = """
            SELECT m.id,
                   m.profile_key,
                   m.nickname,
                   m.gender,
                   m.birth_year,
                   m.comment,
                   m.updated_at,
                   (m.location <-> :location) AS distance,
                   (SELECT COUNT(*) FROM likes l WHERE l.liked_id = m.id) AS likes
            FROM member m
            WHERE m.deleted_at IS NULL
                AND m.id <> :memberId
                AND m.location IS NOT NULL
                AND m.id NOT IN (
                    SELECT b.blocked_id FROM blocks b WHERE b.blocker_id = :memberId
                    UNION
                    SELECT b.blocker_id FROM blocks b WHERE b.blocked_id = :memberId
                )
                $genderCondition
                $cursorCondition
            ORDER BY m.location <-> :location, m.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("memberId", memberId)
            setParameter("location", location)
            setParameter("size", size)

            if (gender == "MALE" || gender == "FEMALE") {
                setParameter("gender", gender)
            }
            if (cursorId != null && cursorDistance != null) {
                setParameter("cursorDistance", cursorDistance)
                setParameter("cursorId", cursorId)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberItemResult)
    }

    private fun toMemberItemResult(row: Array<Any?>): MemberItemResult {
        return MemberItemResult(
            memberId = (row[0] as Number).toLong(),
            profileKey = row[1] as? String,
            nickname = MemberNickname(row[2] as String),
            gender = Gender.valueOf(row[3] as String),
            birthYear = MemberBirthYear((row[4] as Number).toInt()),
            comment = MemberComment(row[5] as String),
            updatedAt = row[6] as LocalDateTime,
            distance = (row[7] as? Number)?.toDouble(),
            likes = (row[8] as Number).toInt(),
        )
    }

    override fun searchByNickname(
        memberId: Long,
        keyword: String,
        cursorId: Long?,
        size: Int
    ): List<MemberItemResponse> {
        val cursorCondition = if (cursorId != null) {
            "AND m.id < :cursorId"
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
                AND m.nickname ILIKE '%' || :keyword || '%'
                AND NOT EXISTS (
                    SELECT 1 FROM blocks
                    WHERE (blocker_id = :requesterId AND blocked_id = m.id)
                       OR (blocked_id = :requesterId AND blocker_id = m.id)
                )
                $cursorCondition
            ORDER BY m.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("requesterId", memberId)
            setParameter("keyword", keyword)
            setParameter("size", size)

            if (cursorId != null) {
                setParameter("cursorId", cursorId)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberItemResponse)
    }

    override fun findDistanceFromLocation(
        location: Point?,
        memberId: Long
    ): Double? {
        if (location == null) {
            return null
        }

        val sql = """
            SELECT (m.location <-> :location)
            FROM member m
            WHERE m.id = :memberId
                AND m.location IS NOT NULL
        """.trimIndent()

        val result = entityManager.createNativeQuery(sql).apply {
            setParameter("location", location)
            setParameter("memberId", memberId)
        }.resultList

        return (result.firstOrNull() as? Number)?.toDouble()
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