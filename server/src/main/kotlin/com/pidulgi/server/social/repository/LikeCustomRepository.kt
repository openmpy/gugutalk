package com.pidulgi.server.social.repository

import com.pidulgi.server.social.repository.result.LikeItemResult

fun interface LikeCustomRepository {

    fun findAllLikesByCursor(
        likerId: Long,
        cursorId: Long?,
        size: Int
    ): List<LikeItemResult>
}