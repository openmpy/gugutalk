package com.pidulgi.server.social.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.social.dto.response.LikeCountResponse
import com.pidulgi.server.social.service.LikeService
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
        val response = likeService.like(likerId, likedId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/social/likes/{likedId}")
    fun unlike(
        @Login likerId: Long,
        @PathVariable likedId: Long
    ): ResponseEntity<LikeCountResponse> {
        val response = likeService.unlike(likerId, likedId)
        return ResponseEntity.ok(response)
    }
}