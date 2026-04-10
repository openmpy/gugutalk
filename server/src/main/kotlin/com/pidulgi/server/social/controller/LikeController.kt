package com.pidulgi.server.social.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.social.dto.response.LikeCountResponse
import com.pidulgi.server.social.service.LikeService
import com.pidulgi.server.social.service.command.LikeMemberCommand
import com.pidulgi.server.social.service.command.UnlikeMemberCommand
import com.pidulgi.server.social.service.query.GetLikedMembersQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class LikeController(

    private val likeService: LikeService,
) {

    @PostMapping("/v1/social/likes/{likedId}")
    fun like(
        @Login likerId: Long,
        @PathVariable likedId: Long
    ): ResponseEntity<LikeCountResponse> {
        val command = LikeMemberCommand(likerId, likedId)
        val response = likeService.like(command)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/social/likes")
    fun getLikedMembers(
        @Login likerId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<SettingResponse>> {
        val query = GetLikedMembersQuery(likerId, cursorId, size)
        val response = likeService.getLikedMembers(query)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/social/likes/{likedId}")
    fun unlike(
        @Login likerId: Long,
        @PathVariable likedId: Long
    ): ResponseEntity<LikeCountResponse> {
        val command = UnlikeMemberCommand(likerId, likedId)
        val response = likeService.unlike(command)
        return ResponseEntity.ok(response)
    }
}