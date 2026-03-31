package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.member.service.PrivateImageGrantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class PrivateImageGrantController(

    private val privateImageGrantService: PrivateImageGrantService,
) {

    @PostMapping("/v1/members/{granteeId}/private-images/grant")
    fun grant(
        @Login granterId: Long,
        @PathVariable granteeId: Long
    ): ResponseEntity<Unit> {
        privateImageGrantService.open(granterId, granteeId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/members/{granteeId}/private-images/grant")
    fun revoke(
        @Login granterId: Long,
        @PathVariable granteeId: Long
    ): ResponseEntity<Unit> {
        privateImageGrantService.close(granterId, granteeId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/members/private-image-grants")
    fun getGrantedMembers(
        @Login granterId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDate: LocalDateTime?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<SettingResponse>> {
        val response = privateImageGrantService.getGrantedMembers(
            granterId, cursorId, cursorDate, size
        )
        return ResponseEntity.ok(response)
    }
}