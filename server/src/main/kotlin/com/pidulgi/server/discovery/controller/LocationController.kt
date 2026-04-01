package com.pidulgi.server.discovery.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.PageResponse
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.discovery.service.LocationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class LocationController(

    private val locationService: LocationService
) {

    @GetMapping("/v1/discovery/location")
    fun getLocationMembers(
        @Login memberId: Long,
        @RequestParam(defaultValue = "ALL") gender: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<PageResponse<MemberDiscoveryResponse>> {
        val response = locationService.getLocationMembers(memberId, gender, page, size)
        return ResponseEntity.ok(response)
    }
}