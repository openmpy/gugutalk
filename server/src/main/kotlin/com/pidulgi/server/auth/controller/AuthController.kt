package com.pidulgi.server.auth.controller

import com.pidulgi.server.auth.dto.request.ActivateRequest
import com.pidulgi.server.auth.dto.request.SignupRequest
import com.pidulgi.server.auth.dto.response.SignupResponse
import com.pidulgi.server.auth.service.AuthService
import com.pidulgi.server.common.auth.Login
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class AuthController(

    private val authService: AuthService,
) {

    @PostMapping("/auth/phone/send")
    fun sendCodeVerificationCode(
        servletRequest: HttpServletRequest,
        @RequestParam phoneNumber: String
    ): ResponseEntity<Unit> {
        authService.sendVerificationCode(servletRequest, phoneNumber)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/auth/signup")
    fun signup(
        @RequestBody request: SignupRequest,
    ): ResponseEntity<SignupResponse> {
        val response = authService.signup(request)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/auth/activate")
    fun activate(
        @Login memberId: Long,
        @RequestBody request: ActivateRequest
    ): ResponseEntity<Unit> {
        authService.activate(memberId, request)
        return ResponseEntity.ok().build()
    }
}