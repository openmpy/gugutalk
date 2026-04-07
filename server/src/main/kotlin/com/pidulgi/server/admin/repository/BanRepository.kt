package com.pidulgi.server.admin.repository

import com.pidulgi.server.admin.entity.Ban
import org.springframework.data.jpa.repository.JpaRepository

interface BanRepository : JpaRepository<Ban, Long> {

    fun findAllByUuid(uuid: String): List<Ban>
}