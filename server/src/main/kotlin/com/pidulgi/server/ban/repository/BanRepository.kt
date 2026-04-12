package com.pidulgi.server.ban.repository

import com.pidulgi.server.ban.entity.Ban
import org.springframework.data.jpa.repository.JpaRepository

interface BanRepository : JpaRepository<Ban, Long> {

    fun existsByUuid(uuid: String): Boolean
}