package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.PrivateImageGrant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface PrivateImageGrantRepository :
    JpaRepository<PrivateImageGrant, Long>,
    PrivateImageGrantCustomRepository {

    fun existsByGranterIdAndGranteeId(granterId: Long, granteeId: Long): Boolean

    fun findByGranterIdAndGranteeId(granterId: Long, granteeId: Long): PrivateImageGrant?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            DELETE FROM private_image_grants
            WHERE granter_id IN :memberIds OR grantee_id IN :memberIds
        """,
        nativeQuery = true
    )
    fun hardDeleteAllByMemberIdIn(memberIds: List<Long>)
}