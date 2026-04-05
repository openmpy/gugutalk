package com.pidulgi.server.social.repository

import com.pidulgi.server.social.entity.Block
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BlockRepository : JpaRepository<Block, Long>, BlockCustomRepository {

    fun existsByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long): Boolean

    fun findByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long): Block?

    @Query(
        value = """
            SELECT b FROM Block b
            WHERE (b.blockerId = :senderId AND b.blockedId = :targetId)
               OR (b.blockerId = :targetId AND b.blockedId = :senderId)
        """
    )
    fun findBlock(senderId: Long, targetId: Long): Block?
}