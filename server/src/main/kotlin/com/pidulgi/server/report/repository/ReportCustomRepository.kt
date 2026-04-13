package com.pidulgi.server.report.repository

import com.pidulgi.server.report.repository.result.ReportAdminItemResult
import java.time.LocalDateTime

fun interface ReportCustomRepository {

    fun findAllReportsForAdminByCursor(
        type: String,
        keyword: String,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ReportAdminItemResult>
}