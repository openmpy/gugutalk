package com.pidulgi.server.common.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.server.ResponseStatusException

class AuthenticationPrincipalArgumentResolver(

    private val jwtProvider: JwtProvider
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Login::class.java)
                && parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val servletRequest = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val accessToken = servletRequest?.let { AuthenticationExtractor.extract(it) }

        if (accessToken == null || jwtProvider.isBlacklist(accessToken)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "토큰에 접근할 수 없습니다.")
        }
        if (!jwtProvider.validateToken(accessToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.")
        }
        return jwtProvider.extractMemberId(accessToken)
    }
}