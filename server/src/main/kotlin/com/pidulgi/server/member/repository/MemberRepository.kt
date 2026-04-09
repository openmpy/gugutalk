package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.vo.MemberNickname
import com.pidulgi.server.member.entity.vo.MemberPhoneNumber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface MemberRepository : JpaRepository<Member, Long>, MemberCustomRepository {

    fun findByUuid(uuid: String): Member?

    fun existsByPhoneNumber(phoneNumber: MemberPhoneNumber): Boolean

    fun existsByNickname(nickname: MemberNickname): Boolean

    fun findByPhoneNumber(phoneNumber: MemberPhoneNumber): Member?

    @Query(
        value = """
            SELECT *
            FROM member m
            WHERE m.id = :id
        """,
        nativeQuery = true
    )
    fun findByIdOrNullNative(id: Long): Member?

    @Query(
        value = """
            SELECT *
            FROM member m
            WHERE :gender = 'ALL' OR m.gender = :gender
            ORDER BY m.updated_at DESC, m.id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true
    )
    fun findAllByPage(
        @Param("gender") gender: String,
        @Param("offset") offset: Int,
        @Param("size") size: Int
    ): List<Member>

    @Query(
        value = """
            SELECT *
            FROM member m
            WHERE m.nickname ILIKE '%' || :keyword || '%'
                AND (:gender = 'ALL' OR m.gender = :gender)
            ORDER BY m.updated_at DESC, m.id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true
    )
    fun findAllByNicknamePage(
        @Param("keyword") keyword: String,
        @Param("gender") gender: String,
        @Param("offset") offset: Int,
        @Param("size") size: Int
    ): List<Member>

    @Query(
        value = """
            SELECT *
            FROM member m
            WHERE m.deleted_at IS NOT NULL
            AND m.deleted_at <= :deletedAt
        """,
        nativeQuery = true
    )
    fun findAllDeleted(@Param("deletedAt") deletedAt: LocalDateTime): List<Member>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            DELETE FROM member
            WHERE id IN :ids
        """,
        nativeQuery = true
    )
    fun hardDeleteByIdIn(@Param("ids") ids: List<Long>)
}