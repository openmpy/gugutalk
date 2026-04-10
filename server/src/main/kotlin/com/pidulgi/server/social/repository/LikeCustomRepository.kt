package com.pidulgi.server.social.repository

import com.pidulgi.server.social.repository.dto.LikeItemResult

fun interface LikeCustomRepository {

    fun findLikesByCursor(
        likerId: Long,
        cursorId: Long?,
        size: Int
    ): List<LikeItemResult>
}