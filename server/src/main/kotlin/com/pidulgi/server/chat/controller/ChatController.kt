package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.service.ChatRoomService
import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class ChatController(

    private val chatRoomService: ChatRoomService,
) {

    @PostMapping("/v1/chat-rooms")
    fun createDirectRoom(
        @Login memberId: Long,
        @RequestParam("targetId") targetId: Long
    ): ResponseEntity<ChatRoomCreateResponse> {
        val response = chatRoomService.createDirectRoom(memberId, targetId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/chat-rooms")
    fun gets(
        @Login memberId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDate: LocalDateTime?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<ChatRoomGetResponse>> {
        val response = chatRoomService.gets(memberId, cursorId, cursorDate, size)
        return ResponseEntity.ok(response)
    }
}