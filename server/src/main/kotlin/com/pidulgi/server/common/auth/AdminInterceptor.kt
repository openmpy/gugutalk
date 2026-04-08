package com.pidulgi.server.common.auth

import com.pidulgi.server.member.entity.type.MemberRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor

class AdminInterceptor(

    private val jwtProvider: JwtProvider
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val accessToken = AuthenticationExtractor.extract(request)

        if (accessToken == null || jwtProvider.isBlacklist(accessToken)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (!jwtProvider.validateToken(accessToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val memberRole = jwtProvider.extractRole(accessToken)
        if (memberRole != MemberRole.ADMIN) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        return true
    }
}