package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.member.service.PrivateImageGrantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class PrivateImageGrantController(

    private val privateImageGrantService: PrivateImageGrantService,
) {

    @PostMapping("/v1/members/{granteeId}/private-images/grant")
    fun open(
        @Login granterId: Long,
        @PathVariable granteeId: Long
    ): ResponseEntity<Unit> {
        privateImageGrantService.open(granterId, granteeId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/members/{granteeId}/private-images/grant")
    fun close(
        @Login granterId: Long,
        @PathVariable granteeId: Long
    ): ResponseEntity<Unit> {
        privateImageGrantService.close(granterId, granteeId)
        return ResponseEntity.ok().build()
    }
}