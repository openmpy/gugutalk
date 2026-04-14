package com.pidulgi.server.ban.repository

import com.pidulgi.server.ban.entity.Ban
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BanRepository : JpaRepository<Ban, Long>, BanCustomRepository {

    fun findByUuid(uuid: String): Ban?

    fun existsByUuid(uuid: String): Boolean
}