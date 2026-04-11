package com.pidulgi.server.social.repository

import com.pidulgi.server.social.repository.result.BlockItemResult

fun interface BlockCustomRepository {

    fun findBlocksByCursor(
        blockerId: Long,
        cursorId: Long?,
        size: Int
    ): List<BlockItemResult>
}