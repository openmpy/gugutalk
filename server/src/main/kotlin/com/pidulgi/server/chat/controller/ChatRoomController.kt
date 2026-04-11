package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.dto.response.ChatRoomSearchResponse
import com.pidulgi.server.chat.service.ChatRoomService
import com.pidulgi.server.chat.service.command.ChatRoomCreateCommand
import com.pidulgi.server.chat.service.query.GetsChatRoomQuery
import com.pidulgi.server.chat.service.query.SearchChatRoomQuery
import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.CursorSimilarityResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class ChatRoomController(

    private val chatRoomService: ChatRoomService,
) {

    @PostMapping("/v1/chat-rooms")
    fun create(
        @Login senderId: Long,
        @RequestParam(required = true) targetId: Long,
    ): ResponseEntity<ChatRoomCreateResponse> {
        val command = ChatRoomCreateCommand(senderId, targetId)
        val response = chatRoomService.create(command)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/chat-rooms/{chatRoomId}")
    fun delete(
        @Login memberId: Long,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<Unit> {
        chatRoomService.delete(memberId, chatRoomId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/chat-rooms")
    fun gets(
        @Login memberId: Long,
        @RequestParam(required = false, defaultValue = "ALL") status: String,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorDate: LocalDateTime?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<ChatRoomGetResponse>> {
        val query = GetsChatRoomQuery(memberId, status, cursorId, cursorDate, size)
        val response = chatRoomService.gets(query)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/v1/chat-rooms/{chatRoomId}")
    fun markAsRead(
        @Login memberId: Long,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<Unit> {
        chatRoomService.markAsRead(memberId, chatRoomId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/chat-rooms/search")
    fun search(
        @Login memberId: Long,
        @RequestParam(required = true) nickname: String,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorSimilarity: Double?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorSimilarityResponse<ChatRoomSearchResponse>> {
        val query = SearchChatRoomQuery(memberId, nickname, cursorId, cursorSimilarity, size)
        val response = chatRoomService.search(query)
        return ResponseEntity.ok(response)
    }
}