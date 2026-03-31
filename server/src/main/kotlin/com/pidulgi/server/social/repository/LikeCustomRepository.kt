package com.pidulgi.server.social.repository

import com.pidulgi.server.social.repository.dto.LikeItemResponse
import java.time.LocalDateTime

interface LikeCustomRepository {

    fun findLikesByCursor(
        likerId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<LikeItemResponse>
}