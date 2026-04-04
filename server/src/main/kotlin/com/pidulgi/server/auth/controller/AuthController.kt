package com.pidulgi.server.auth.controller

import com.pidulgi.server.auth.dto.request.ActivateRequest
import com.pidulgi.server.auth.dto.request.LoginRequest
import com.pidulgi.server.auth.dto.request.SignupRequest
import com.pidulgi.server.auth.dto.request.ValidateRequest
import com.pidulgi.server.auth.dto.response.LoginResponse
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

    @PostMapping("/v1/auth/phone/send")
    fun sendCodeVerificationCode(
        servletRequest: HttpServletRequest,
        @RequestParam phoneNumber: String
    ): ResponseEntity<Unit> {
        authService.sendVerificationCode(servletRequest, phoneNumber)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/v1/auth/signup")
    fun signup(
        @RequestBody request: SignupRequest,
    ): ResponseEntity<SignupResponse> {
        val response = authService.signup(request)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/v1/auth/activate")
    fun activate(
        @Login memberId: Long,
        @RequestBody request: ActivateRequest
    ): ResponseEntity<Unit> {
        authService.activate(memberId, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/v1/auth/validate")
    fun validate(
        @Login memberId: Long,
        @RequestBody request: ValidateRequest
    ): ResponseEntity<Unit> {
        authService.validate(memberId, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/v1/auth/login")
    fun login(
        @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/auth/logout")
    fun logout(
        servletRequest: HttpServletRequest,
        @RequestParam(value = "refreshToken", required = true) refreshToken: String
    ): ResponseEntity<Unit> {
        authService.logout(servletRequest, refreshToken)
        return ResponseEntity.ok().build()
    }
}