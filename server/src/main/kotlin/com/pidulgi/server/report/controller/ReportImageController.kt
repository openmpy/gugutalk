package com.pidulgi.server.report.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.s3.dto.response.PresignedUrlsResponse
import com.pidulgi.server.report.dto.request.ReportGetPresignedUrlsRequest
import com.pidulgi.server.report.service.ReportImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class ReportImageController(

    private val reportImageService: ReportImageService,
) {

    @PostMapping("/v1/reports/images/presigned")
    fun getPresignedUrls(
        @Login memberId: Long,
        @RequestBody request: ReportGetPresignedUrlsRequest
    ): ResponseEntity<PresignedUrlsResponse> {
        val response = reportImageService.getPresignedUrls(memberId, request)
        return ResponseEntity.ok(response)
    }
}