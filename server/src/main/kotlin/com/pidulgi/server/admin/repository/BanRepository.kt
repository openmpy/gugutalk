package com.pidulgi.server.admin.repository

import com.pidulgi.server.admin.entity.Ban
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BanRepository : JpaRepository<Ban, Long> {

    @Query(
        value = """
            SELECT *
            FROM ban b
            ORDER BY b.created_at DESC, b.id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true
    )
    fun findAllByPage(
        @Param("offset") offset: Int,
        @Param("size") size: Int
    ): List<Ban>
}