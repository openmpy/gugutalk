package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.member.dto.request.MemberUpdateLocationRequest
import com.pidulgi.server.member.dto.request.MemberWithdrawRequest
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class MemberController(

    private val memberService: MemberService
) {

    @GetMapping("/v1/members/me")
    fun getMe(
        @Login memberId: Long
    ): ResponseEntity<MemberGetMeResponse> {
        val response = memberService.getMe(memberId)
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
    ): ResponseEntity<Unit> {
        memberService.bump(memberId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/members/me/location")
    fun updateLocation(
        @Login memberId: Long,
        @RequestBody request: MemberUpdateLocationRequest,
    ): ResponseEntity<Unit> {
        memberService.updateLocation(memberId, request)
        return ResponseEntity.ok().build()
    }
}