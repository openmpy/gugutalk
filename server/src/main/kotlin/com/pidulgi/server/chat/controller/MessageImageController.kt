package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.request.MediaGetPresignedUrlsRequest
import com.pidulgi.server.chat.service.MessageImageService
import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.s3.dto.response.PresignedUrlsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class MessageImageController(

    private val messageImageService: MessageImageService,
) {

    @PostMapping("/v1/chat-rooms/{chatRoomId}/presigned")
    fun getPresignedUrls(
        @Login memberId: Long,
        @PathVariable chatRoomId: Long,
        @RequestBody request: MediaGetPresignedUrlsRequest,
    ): ResponseEntity<PresignedUrlsResponse> {
        val response = messageImageService.getPresignedUrls(memberId, chatRoomId, request)
        return ResponseEntity.ok(response)
    }
}