package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}