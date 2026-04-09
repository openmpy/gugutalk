package com.pidulgi.server.social.repository

import com.pidulgi.server.social.entity.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface LikeRepository : JpaRepository<Like, Long>, LikeCustomRepository {

    fun existsByLikerIdAndLikedId(likerId: Long, likedId: Long): Boolean

    fun findByLikerIdAndLikedId(likerId: Long, likedId: Long): Like?

    fun countByLikedId(likedId: Long): Long

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            DELETE FROM likes
            WHERE liker_id IN :memberIds OR liked_id IN :memberIds
        """,
        nativeQuery = true
    )
    fun hardDeleteAllByMemberIdIn(memberIds: List<Long>)
}