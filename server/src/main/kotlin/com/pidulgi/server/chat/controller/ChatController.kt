package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetTargetResponse
import com.pidulgi.server.chat.dto.response.MessageGetResponse
import com.pidulgi.server.chat.service.ChatRoomService
import com.pidulgi.server.chat.service.MessageService
import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class ChatController(

    private val chatRoomService: ChatRoomService,
    private val messageService: MessageService,
) {

    @PostMapping("/v1/chat-rooms")
    fun createDirectRoom(
        @Login memberId: Long,
        @RequestParam("targetId") targetId: Long
    ): ResponseEntity<ChatRoomCreateResponse> {
        val response = chatRoomService.createDirectRoom(memberId, targetId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/chat-rooms/{chatRoomId}")
    fun deleteDirectRoom(
        @Login memberId: Long,
        @PathVariable chatRoomId: Long
    ): ResponseEntity<Unit> {
        chatRoomService.deleteDirectRoom(memberId, chatRoomId)
        return ResponseEntity.ok().build()
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

    @GetMapping("/v1/chat-rooms/{chatRoomId}/member")
    fun getTarget(
        @Login memberId: Long,
        @PathVariable("chatRoomId") chatRoomId: Long,
    ): ResponseEntity<ChatRoomGetTargetResponse> {
        val response = chatRoomService.getTarget(memberId, chatRoomId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/chat-rooms/{chatRoomId}/messages")
    fun getMessages(
        @Login memberId: Long,
        @PathVariable("chatRoomId") chatRoomId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDate: LocalDateTime?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<MessageGetResponse>> {
        val response = messageService.gets(memberId, chatRoomId, cursorId, cursorDate, size)
        return ResponseEntity.ok(response)
    }
}