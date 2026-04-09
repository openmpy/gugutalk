package com.pidulgi.server.social.repository

import com.pidulgi.server.social.repository.dto.BlockItemResponse

interface BlockCustomRepository {

    fun findBlocksByCursor(
        blockerId: Long,
        cursorId: Long?,
        size: Int
    ): List<BlockItemResponse>
}