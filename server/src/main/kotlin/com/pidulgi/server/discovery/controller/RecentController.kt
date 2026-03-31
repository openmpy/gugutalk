package com.pidulgi.server.discovery.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.discovery.service.RecentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class RecentController(

    private val recentService: RecentService
) {

    @GetMapping("/v1/discovery/recent")
    fun getRecentMembers(
        @Login memberId: Long,
        @RequestParam(defaultValue = "ALL") gender: String,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDate: LocalDateTime?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<MemberDiscoveryResponse>> {
        val response = recentService.getRecentMembers(
            memberId,
            gender,
            cursorId,
            cursorDate,
            size
        )
        return ResponseEntity.ok(response)
    }
}