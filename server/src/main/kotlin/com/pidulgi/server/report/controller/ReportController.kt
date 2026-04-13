package com.pidulgi.server.report.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.report.dto.request.ReportCreateRequest
import com.pidulgi.server.report.service.ReportService
import com.pidulgi.server.report.service.command.ReportCreateCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class ReportController(

    private val reportService: ReportService,
) {

    @PostMapping("/v1/reports/{reportedId}")
    fun create(
        @Login reporterId: Long,
        @PathVariable reportedId: Long,
        @RequestBody request: ReportCreateRequest
    ): ResponseEntity<Unit> {
        val command = ReportCreateCommand(reporterId, reportedId)
        reportService.create(command, request)
        return ResponseEntity.ok().build()
    }
}