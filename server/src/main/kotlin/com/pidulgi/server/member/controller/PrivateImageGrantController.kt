package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.member.service.PrivateImageGrantService
import com.pidulgi.server.member.service.command.PrivateImageGrantCloseCommand
import com.pidulgi.server.member.service.command.PrivateImageGrantOpenCommand
import com.pidulgi.server.member.service.query.GetGrantedMembersQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class PrivateImageGrantController(

    private val privateImageGrantService: PrivateImageGrantService,
) {

    @PostMapping("/v1/members/{granteeId}/private-image-grant")
    fun open(
        @Login granterId: Long,
        @PathVariable granteeId: Long
    ): ResponseEntity<Unit> {
        val command = PrivateImageGrantOpenCommand(granterId, granteeId)
        privateImageGrantService.open(command)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/members/private-image-grants")
    fun getGrantedMembers(
        @Login granterId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<SettingResponse>> {
        val query = GetGrantedMembersQuery(granterId, cursorId, size)
        val response = privateImageGrantService.getGrantedMembers(query)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/members/{granteeId}/private-image-grant")
    fun close(
        @Login granterId: Long,
        @PathVariable granteeId: Long
    ): ResponseEntity<Unit> {
        val command = PrivateImageGrantCloseCommand(granterId, granteeId)
        privateImageGrantService.close(command)
        return ResponseEntity.ok().build()
    }
}