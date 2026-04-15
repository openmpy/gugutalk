package com.pidulgi.server.ban.repository

import com.pidulgi.server.ban.entity.Ban
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BanRepository : JpaRepository<Ban, Long>, BanCustomRepository {

    fun findByUuid(uuid: String): Ban?

    fun findByPhoneNumber(phoneNumber: String): Ban?

    fun existsByUuid(uuid: String): Boolean

    fun findAllByCreatedAtBefore(createdAt: LocalDateTime): List<Ban>
}