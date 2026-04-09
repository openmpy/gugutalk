package com.pidulgi.server.point.repository

import com.pidulgi.server.point.entity.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PointRepository : JpaRepository<Point, Long> {

    fun findByMemberId(memberId: Long): Point?

    @Modifying
    @Query("DELETE FROM Point p WHERE p.memberId IN :memberIds")
    fun deleteAllByMemberIds(@Param("memberIds") memberIds: List<Long>)
}