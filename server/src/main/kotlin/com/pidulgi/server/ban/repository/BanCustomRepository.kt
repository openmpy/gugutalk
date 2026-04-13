package com.pidulgi.server.ban.repository

import com.pidulgi.server.ban.repository.result.BanAdminItemResult
import java.time.LocalDateTime

fun interface BanCustomRepository {

    fun findAllBansForAdminByCursor(
        type: String,
        keyword: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<BanAdminItemResult>
}