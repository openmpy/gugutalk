package com.pidulgi.server.common.config

import com.pidulgi.server.common.auth.AdminInterceptor
import com.pidulgi.server.common.auth.AuthenticationPrincipalArgumentResolver
import com.pidulgi.server.common.auth.JwtProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(

    @Value("\${cors.path-pattern}")
    private val pathPattern: String,

    @Value("\${cors.origins}")
    private val allowedOrigins: Array<String>,

    @Value("\${cors.methods}")
    private val allowedMethods: Array<String>,

    @Value("\${cors.headers}")
    private val allowedHeaders: Array<String>,

    @Value("\${cors.allow-credentials}")
    private val allowCredentials: Boolean,

    @Value("\${cors.max-age}")
    private val maxAge: Long,

    private val jwtProvider: JwtProvider,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AdminInterceptor(jwtProvider))
            .addPathPatterns("/api/*/admin/**")
            .excludePathPatterns("/api/*/admin/login")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(argumentResolver())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping(pathPattern)
            .allowedOrigins(*allowedOrigins)
            .allowedMethods(*allowedMethods)
            .allowedHeaders(*allowedHeaders)
            .allowCredentials(allowCredentials)
            .maxAge(maxAge)
    }

    private fun argumentResolver(): AuthenticationPrincipalArgumentResolver {
        return AuthenticationPrincipalArgumentResolver(jwtProvider)
    }
}