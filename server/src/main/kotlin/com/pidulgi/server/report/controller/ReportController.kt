package com.pidulgi.server.report.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.report.dto.ReportCreateRequest
import com.pidulgi.server.report.service.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class ReportController(

    private val reportService: ReportService,
) {

    @PutMapping("/v1/reports/{reportedId}")
    fun create(
        @Login reporterId: Long,
        @PathVariable reportedId: Long,
        @RequestBody request: ReportCreateRequest
    ): ResponseEntity<Unit> {
        reportService.create(reporterId, reportedId, request)
        return ResponseEntity.ok().build()
    }
}