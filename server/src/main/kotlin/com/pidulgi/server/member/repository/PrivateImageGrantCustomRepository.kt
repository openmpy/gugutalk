package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.dto.PrivateImageGrantItemResponse

interface PrivateImageGrantCustomRepository {

    fun findGrantsByCursor(
        granterId: Long,
        cursorId: Long?,
        size: Int
    ): List<PrivateImageGrantItemResponse>
}