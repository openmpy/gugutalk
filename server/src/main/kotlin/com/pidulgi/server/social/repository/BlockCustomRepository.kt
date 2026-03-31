package com.pidulgi.server.social.repository

import com.pidulgi.server.social.repository.dto.BlockItemResponse
import java.time.LocalDateTime

interface BlockCustomRepository {

    fun findBlocksByCursor(
        blockerId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<BlockItemResponse>
}