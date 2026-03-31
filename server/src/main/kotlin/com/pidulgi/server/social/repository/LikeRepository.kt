package com.pidulgi.server.social.repository

import com.pidulgi.server.social.entity.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long>, LikeCustomRepository {

    fun existsByLikerIdAndLikedId(likerId: Long, liked: Long): Boolean

    fun findByLikerIdAndLikedId(likerId: Long, liked: Long): Like?

    fun countByLikedId(liked: Long): Long
}