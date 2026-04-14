package com.pidulgi.server.ban.repository

import com.pidulgi.server.ban.entity.BanHistory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BanHistoryRepository : JpaRepository<BanHistory, Long> {

    fun findAllByUuid(uuid: String, pageable: Pageable): List<BanHistory>
}