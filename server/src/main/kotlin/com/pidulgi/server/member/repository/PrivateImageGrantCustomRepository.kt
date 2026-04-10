package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.dto.PrivateImageGrantItemResult

fun interface PrivateImageGrantCustomRepository {

    fun findGrantsByCursor(
        granterId: Long,
        cursorId: Long?,
        size: Int
    ): List<PrivateImageGrantItemResult>
}