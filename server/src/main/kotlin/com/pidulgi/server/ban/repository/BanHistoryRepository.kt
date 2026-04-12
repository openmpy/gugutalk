package com.pidulgi.server.ban.repository

import com.pidulgi.server.ban.entity.BanHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BanHistoryRepository : JpaRepository<BanHistory, Long>