package com.pidulgi.server.social.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.social.service.BlockService
import com.pidulgi.server.social.service.command.BlockAddMemberCommand
import com.pidulgi.server.social.service.command.BlockRemoveMemberCommand
import com.pidulgi.server.social.service.query.GetBlockedMembersQuery
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
        val command = BlockAddMemberCommand(blockerId, blockedId)
        blockService.add(command)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/social/blocks")
    fun getBlockedMembers(
        @Login blockerId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<SettingResponse>> {
        val query = GetBlockedMembersQuery(blockerId, cursorId, size)
        val response = blockService.getBlockedMembers(query)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/social/blocks/{blockedId}")
    fun remove(
        @Login blockerId: Long,
        @PathVariable blockedId: Long
    ): ResponseEntity<Unit> {
        val command = BlockRemoveMemberCommand(blockerId, blockedId)
        blockService.remove(command)
        return ResponseEntity.ok().build()
    }
}