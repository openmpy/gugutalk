package com.pidulgi.server.member.repository

import com.pidulgi.server.member.repository.dto.PrivateImageGrantItemResponse
import java.time.LocalDateTime

interface PrivateImageGrantCustomRepository {

    fun findGrantsByCursor(
        granterId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<PrivateImageGrantItemResponse>
}