package com.pidulgi.server.point.repository

import com.pidulgi.server.point.entity.Point
import org.springframework.data.jpa.repository.JpaRepository

interface PointRepository : JpaRepository<Point, Long> {

    fun findByMemberId(memberId: Long): Point?
}