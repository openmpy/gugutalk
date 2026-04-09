package com.pidulgi.server.point.repository

import com.pidulgi.server.point.entity.PointTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PointTransactionRepository : JpaRepository<PointTransaction, Long> {

    @Modifying
    @Query("DELETE FROM PointTransaction pt WHERE pt.memberId IN :memberIds")
    fun deleteAllByMemberIds(@Param("memberIds") memberIds: List<Long>)
}