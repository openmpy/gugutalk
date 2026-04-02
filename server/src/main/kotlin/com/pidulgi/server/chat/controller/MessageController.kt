package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.response.MessageGetMemberResponse
import com.pidulgi.server.chat.dto.response.MessageGetResponse
import com.pidulgi.server.chat.service.MessageService
import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class MessageController(

    private val messageService: MessageService,
) {

    @GetMapping("/v1/chat-rooms/{chatRoomId}/messages")
    fun gets(
        @Login memberId: Long,
        @PathVariable chatRoomId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDate: LocalDateTime?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<MessageGetResponse>> {
        val response = messageService.gets(memberId, chatRoomId, cursorId, cursorDate, size)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/chat-rooms/{chatRoomId}/member")
    fun getMember(
        @Login memberId: Long,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<MessageGetMemberResponse> {
        val response = messageService.getMember(memberId, chatRoomId)
        return ResponseEntity.ok(response)
    }
}