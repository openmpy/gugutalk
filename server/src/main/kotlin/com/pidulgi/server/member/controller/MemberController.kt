package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.dto.CursorSimilarityResponse
import com.pidulgi.server.member.dto.request.MemberBumpRequest
import com.pidulgi.server.member.dto.request.MemberUpdateCommentRequest
import com.pidulgi.server.member.dto.request.MemberUpdateProfileRequest
import com.pidulgi.server.member.dto.request.MemberWithdrawRequest
import com.pidulgi.server.member.dto.response.MemberGetChatEnabledResponse
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.dto.response.MemberGetResponse
import com.pidulgi.server.member.dto.response.MemberSearchResponse
import com.pidulgi.server.member.service.MemberService
import com.pidulgi.server.member.service.query.SearchByNicknameQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class MemberController(

    private val memberService: MemberService,
) {

    @GetMapping("/v1/members/me")
    fun getMe(
        @Login memberId: Long
    ): ResponseEntity<MemberGetMeResponse> {
        val response = memberService.getMe(memberId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/members/{targetId}")
    fun getMember(
        @Login memberId: Long,
        @PathVariable targetId: Long,
    ): ResponseEntity<MemberGetResponse> {
        val response = memberService.getMember(memberId, targetId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/members/me")
    fun withdraw(
        @Login memberId: Long,
        @RequestBody request: MemberWithdrawRequest
    ): ResponseEntity<Unit> {
        memberService.withdraw(memberId, request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/members/me/bump")
    fun bump(
        @Login memberId: Long,
        @RequestBody request: MemberBumpRequest,
    ): ResponseEntity<Unit> {
        memberService.bump(memberId, request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/members/me/profile")
    fun updateProfile(
        @Login memberId: Long,
        @RequestBody request: MemberUpdateProfileRequest,
    ): ResponseEntity<Unit> {
        memberService.updateProfile(memberId, request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/members/me/comment")
    fun updateComment(
        @Login memberId: Long,
        @RequestBody request: MemberUpdateCommentRequest,
    ): ResponseEntity<Unit> {
        memberService.updateComment(memberId, request)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/members/search")
    fun search(
        @Login memberId: Long,
        @RequestParam(required = true) nickname: String,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorSimilarity: Double?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorSimilarityResponse<MemberSearchResponse>> {
        val query = SearchByNicknameQuery(memberId, nickname, cursorId, cursorSimilarity, size)
        val response = memberService.searchByNickname(query)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/members/chat-enabled")
    fun getChatEnabled(
        @Login memberId: Long,
    ): ResponseEntity<MemberGetChatEnabledResponse> {
        val response = memberService.getChatEnabled(memberId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/v1/members/chat-enabled")
    fun toggleChatEnabled(
        @Login memberId: Long,
    ): ResponseEntity<MemberGetChatEnabledResponse> {
        val response = memberService.toggleChatEnabled(memberId)
        return ResponseEntity.ok(response)
    }
}