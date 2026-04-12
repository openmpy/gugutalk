package com.pidulgi.server.member.repository.impl

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.vo.MemberBirthYear
import com.pidulgi.server.member.entity.vo.MemberComment
import com.pidulgi.server.member.entity.vo.MemberNickname
import com.pidulgi.server.member.repository.MemberCustomRepository
import com.pidulgi.server.member.repository.result.MemberItemResult
import com.pidulgi.server.member.repository.result.MemberSearchItemResult
import jakarta.persistence.EntityManager
import org.locationtech.jts.geom.Point
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MemberCustomRepositoryImpl(

    private val entityManager: EntityManager,
) : MemberCustomRepository {

    // 회원 목록 - 최근 업데이트 순
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
            "m.location <-> :location"
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
                setParameter("cursorId", cursorId)
                setParameter("cursorDate", cursorDate)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberItemResult)
    }

    // 회원 목록 - 거리 순
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
                    (m.location <-> :location) < :cursorDistance
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
                setParameter("cursorId", cursorId)
                setParameter("cursorDistance", cursorDistance)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberItemResult)
    }

    // 회원 검색 - 닉네임 유사도
    override fun findAllMembersByNicknameWithCursor(
        memberId: Long,
        nickname: String,
        location: Point?,
        cursorId: Long?,
        cursorSimilarity: Double?,
        size: Int
    ): List<MemberSearchItemResult> {
        val cursorCondition = if (cursorId != null && cursorSimilarity != null) {
            """
            AND (
                similarity(m.nickname, :nickname) < :cursorSimilarity
                OR (similarity(m.nickname, :nickname) = :cursorSimilarity AND m.id < :cursorId)
            )
            """.trimIndent()
        } else ""

        val distanceExpression = if (location != null) {
            "m.location <-> :location"
        } else {
            "NULL"
        }

        val sql = """
            SELECT m.id,
                   m.profile_key,
                   m.nickname,
                   m.gender,
                   m.birth_year,
                   m.comment,
                   m.updated_at,
                   $distanceExpression AS distance,
                   (SELECT COUNT(*) FROM likes l WHERE l.liked_id = m.id) AS likes,
                   similarity(m.nickname, :nickname) AS similarity_score
            FROM member m
            WHERE m.deleted_at IS NULL
                AND m.id <> :memberId
                AND m.nickname % :nickname
                AND m.id NOT IN (
                    SELECT b.blocked_id FROM blocks b WHERE b.blocker_id = :memberId
                    UNION
                    SELECT b.blocker_id FROM blocks b WHERE b.blocked_id = :memberId
                )
                $cursorCondition
            ORDER BY similarity_score DESC, m.id DESC
            LIMIT :size
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql).apply {
            setParameter("memberId", memberId)
            setParameter("nickname", nickname)
            setParameter("size", size)

            if (location != null) {
                setParameter("location", location)
            }
            if (cursorId != null && cursorSimilarity != null) {
                setParameter("cursorId", cursorId)
                setParameter("cursorSimilarity", cursorSimilarity)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return (query.resultList as List<Array<Any?>>).map(::toMemberSearchItemResult)
    }

    // 회원 사이 거리
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

    private fun toMemberSearchItemResult(row: Array<Any?>): MemberSearchItemResult {
        return MemberSearchItemResult(
            memberId = (row[0] as Number).toLong(),
            profileKey = row[1] as? String,
            nickname = MemberNickname(row[2] as String),
            gender = Gender.valueOf(row[3] as String),
            birthYear = MemberBirthYear((row[4] as Number).toInt()),
            comment = MemberComment(row[5] as String),
            updatedAt = row[6] as LocalDateTime,
            distance = (row[7] as? Number)?.toDouble(),
            likes = (row[8] as Number).toInt(),
            similarityScore = (row[9] as Number).toDouble(),
        )
    }
}