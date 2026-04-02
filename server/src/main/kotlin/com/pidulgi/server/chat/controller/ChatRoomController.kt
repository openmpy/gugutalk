package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.service.ChatRoomService
import com.pidulgi.server.common.auth.Login
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class ChatRoomController(

    private val chatRoomService: ChatRoomService,
) {

    @PostMapping("/v1/chat-rooms")
    fun create(
        @Login senderId: Long,
        @RequestParam(value = "targetId") targetId: Long,
    ): ResponseEntity<ChatRoomCreateResponse> {
        val response = chatRoomService.create(senderId, targetId)
        return ResponseEntity.ok(response)
    }
}