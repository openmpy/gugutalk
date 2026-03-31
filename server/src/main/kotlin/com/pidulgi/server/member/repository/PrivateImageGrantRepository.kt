package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.PrivateImageGrant
import org.springframework.data.jpa.repository.JpaRepository

interface PrivateImageGrantRepository :
    JpaRepository<PrivateImageGrant, Long>,
    PrivateImageGrantCustomRepository {

    fun existsByGranterIdAndGranteeId(granterId: Long, granteeId: Long): Boolean

    fun findByGranterIdAndGranteeId(granterId: Long, granteeId: Long): PrivateImageGrant?
}