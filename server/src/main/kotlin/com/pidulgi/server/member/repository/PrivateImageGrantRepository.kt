package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.PrivateImageGrant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PrivateImageGrantRepository :
    JpaRepository<PrivateImageGrant, Long>,
    PrivateImageGrantCustomRepository {

    fun existsByGranterIdAndGranteeId(granterId: Long, granteeId: Long): Boolean

    fun findByGranterIdAndGranteeId(granterId: Long, granteeId: Long): PrivateImageGrant?

    @Modifying
    @Query("DELETE FROM PrivateImageGrant p WHERE p.granterId IN :memberIds OR p.granteeId IN :memberIds")
    fun deleteAllByMemberIds(@Param("memberIds") memberIds: List<Long>)
}