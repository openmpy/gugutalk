package com.pidulgi.server.common.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

const val AUTHORIZATION_HEADER_PREFIX = "Bearer "

class AuthenticationExtractor {

    companion object {

        fun extract(request: HttpServletRequest): String? {
            val header = request.getHeader(HttpHeaders.AUTHORIZATION)

            if (header == null || !header.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
                return null
            }
            return header.substring(AUTHORIZATION_HEADER_PREFIX.length)
        }
    }
}