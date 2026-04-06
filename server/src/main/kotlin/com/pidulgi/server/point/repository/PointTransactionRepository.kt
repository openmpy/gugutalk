package com.pidulgi.server.point.repository

import com.pidulgi.server.point.entity.PointTransaction
import org.springframework.data.jpa.repository.JpaRepository

interface PointTransactionRepository : JpaRepository<PointTransaction, Long>