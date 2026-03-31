package com.pidulgi.server.common.auth

import com.pidulgi.server.auth.service.AUTH_ACCESS_TOKEN_BLACKLIST_KEY
import io.jsonwebtoken.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

const val ACCESS_TOKEN_EXPIRE_HOURS: Long = 24

@Component
class JwtProvider(

    private val redisTemplate: StringRedisTemplate
) {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private val log: Logger by lazy { LoggerFactory.getLogger("JwtProvider") }

    private val accessTokenExpiry = Duration.ofHours(ACCESS_TOKEN_EXPIRE_HOURS)
    private val refreshTokenExpiry = Duration.ofDays(30)

    fun generateAccessToken(memberId: Long): String {
        return Jwts.builder()
            .setSubject(memberId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiry.toMillis()))
            .claim("type", "access")
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun generateRefreshToken(memberId: Long): String {
        return Jwts.builder()
            .setSubject(memberId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpiry.toMillis()))
            .claim("type", "refresh")
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean =
        runCatching {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        }.onFailure { e ->
            when (e) {
                is ExpiredJwtException -> log.warn("토큰이 만료되었습니다. {}", e.message)
                is UnsupportedJwtException -> log.warn("지원되지 않는 토큰입니다. {}", e.message)
                is MalformedJwtException -> log.warn("형식이 잘못된 토큰입니다. {}", e.message)
                is SecurityException -> log.warn("유효하지 않은 서명입니다. {}", e.message)
                is IllegalArgumentException -> log.warn("토큰 클레임이 비어 있습니다. {}", e.message)
                else -> log.warn("알 수 없는 오류가 발생했습니다. {}", e.message)
            }
        }.isSuccess

    fun extractMemberId(token: String): Long {
        val payload = getPayload(token)
        return payload.subject.toLong()
    }

    fun isBlacklist(accessToken: String): Boolean {
        return redisTemplate.opsForValue().get(
            AUTH_ACCESS_TOKEN_BLACKLIST_KEY + accessToken
        ) != null
    }

    private fun getPayload(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
    }
}