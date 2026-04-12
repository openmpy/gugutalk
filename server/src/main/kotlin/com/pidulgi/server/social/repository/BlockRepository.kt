package com.pidulgi.server.social.repository

import com.pidulgi.server.social.entity.Block
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BlockRepository : JpaRepository<Block, Long>, BlockCustomRepository {

    fun existsByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long): Boolean

    fun findByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long): Block?

    @Query(
        value = """
            SELECT COUNT(b) > 0
            FROM Block b
            WHERE (b.blockerId = :senderId AND b.blockedId = :targetId)
               OR (b.blockerId = :targetId AND b.blockedId = :senderId)
        """
    )
    fun existsBlock(senderId: Long, targetId: Long): Boolean

    @Modifying
    @Query("DELETE FROM Block b WHERE b.blockerId IN :memberIds OR b.blockedId IN :memberIds")
    fun deleteAllByMemberIds(@Param("memberIds") memberIds: List<Long>)
}