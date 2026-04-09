package com.pidulgi.server.social.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.social.service.BlockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class BlockController(
    private val blockService: BlockService,
) {

    @PostMapping("/v1/social/blocks/{blockedId}")
    fun add(
        @Login blockerId: Long,
        @PathVariable blockedId: Long
    ): ResponseEntity<Unit> {
        blockService.add(blockerId, blockedId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/social/blocks/{blockedId}")
    fun remove(
        @Login blockerId: Long,
        @PathVariable blockedId: Long
    ): ResponseEntity<Unit> {
        blockService.remove(blockerId, blockedId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/social/blocks")
    fun getBlockedMembers(
        @Login blockerId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<SettingResponse>> {
        val response = blockService.getBlockedMembers(blockerId, cursorId, size)
        return ResponseEntity.ok(response)
    }
}