package com.pidulgi.server.discovery.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorDistanceResponse
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.discovery.service.LocationService
import com.pidulgi.server.discovery.service.query.GetLocationMembersQuery
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
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDistance: Double?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorDistanceResponse<MemberDiscoveryResponse>> {
        val query = GetLocationMembersQuery(memberId, gender, cursorId, cursorDistance, size)
        val response = locationService.getLocationMembers(query)
        return ResponseEntity.ok(response)
    }
}