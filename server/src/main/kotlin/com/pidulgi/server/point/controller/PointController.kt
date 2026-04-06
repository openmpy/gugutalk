package com.pidulgi.server.point.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.point.dto.response.PointGetResponse
import com.pidulgi.server.point.service.PointService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class PointController(

    private val pointService: PointService,
) {

    @PostMapping("/v1/points/attendance")
    fun earnByAttendance(
        @Login memberId: Long,
    ): ResponseEntity<Unit> {
        pointService.earnByAttendance(memberId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/v1/points/ad-reward")
    fun earnByAdReward(
        @Login memberId: Long,
    ): ResponseEntity<Unit> {
        pointService.earnByAdReward(memberId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/points")
    fun get(
        @Login memberId: Long,
    ): ResponseEntity<PointGetResponse> {
        val response = pointService.get(memberId)
        return ResponseEntity.ok(response)
    }
}