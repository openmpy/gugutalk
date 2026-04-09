package com.pidulgi.server.social.repository

import com.pidulgi.server.social.entity.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LikeRepository : JpaRepository<Like, Long>, LikeCustomRepository {

    fun existsByLikerIdAndLikedId(likerId: Long, likedId: Long): Boolean

    fun findByLikerIdAndLikedId(likerId: Long, likedId: Long): Like?

    fun countByLikedId(likedId: Long): Long

    @Modifying
    @Query("DELETE FROM Like l WHERE l.likerId IN :memberIds OR l.likedId IN :memberIds")
    fun deleteAllByMemberIds(@Param("memberIds") memberIds: List<Long>)
}