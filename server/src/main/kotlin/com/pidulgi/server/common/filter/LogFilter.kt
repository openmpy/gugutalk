package com.pidulgi.server.common.filter

import com.pidulgi.server.common.auth.AuthenticationExtractor
import com.pidulgi.server.common.auth.JwtProvider
import com.pidulgi.server.common.util.ClientIpExtractor
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LogFilter(

    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI

        if (uri.startsWith("/api")) {
            val method = request.method
            val ip = ClientIpExtractor.extract(request)

            val accessToken = AuthenticationExtractor.extract(request)

            val memberId: Long?
            val nickname: String?

            if (accessToken != null && jwtProvider.validateToken(accessToken)) {
                memberId = jwtProvider.extractMemberId(accessToken)
                nickname = jwtProvider.extractNickname(accessToken)
            } else {
                memberId = null
                nickname = null
            }

            log.info { "request method = $method, uri = $uri, ip = $ip, memberId = $memberId, nickname = $nickname" }
        }
        filterChain.doFilter(request, response)
    }
}