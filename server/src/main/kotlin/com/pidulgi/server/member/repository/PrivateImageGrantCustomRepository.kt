package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.result.PrivateImageGrantItemResult

fun interface PrivateImageGrantCustomRepository {

    fun findAllGrantsByCursor(
        granterId: Long,
        cursorId: Long?,
        size: Int
    ): List<PrivateImageGrantItemResult>
}