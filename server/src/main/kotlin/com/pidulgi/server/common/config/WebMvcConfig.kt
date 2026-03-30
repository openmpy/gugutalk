package com.pidulgi.server.common.config

import com.pidulgi.server.common.auth.AuthenticationPrincipalArgumentResolver
import com.pidulgi.server.common.auth.JwtProvider
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(

    private val jwtProvider: JwtProvider,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(argumentResolver())
    }

    private fun argumentResolver(): AuthenticationPrincipalArgumentResolver {
        return AuthenticationPrincipalArgumentResolver(jwtProvider)
    }
}