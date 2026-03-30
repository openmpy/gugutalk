package com.pidulgi.server.auth.controller

import com.pidulgi.server.auth.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class AuthController(

    private val authService: AuthService,
) {

    @PostMapping("/auth/verification-code")
    fun sendCodeVerificationCode(
        servletRequest: HttpServletRequest,
        @RequestParam phoneNumber: String
    ): ResponseEntity<Unit> {
        authService.sendVerificationCode(servletRequest, phoneNumber)
        return ResponseEntity.ok().build()
    }
}