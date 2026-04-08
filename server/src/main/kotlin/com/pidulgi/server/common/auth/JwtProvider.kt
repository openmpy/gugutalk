package com.pidulgi.server.common.auth

import com.pidulgi.server.auth.service.AUTH_ACCESS_TOKEN_BLACKLIST_KEY
import com.pidulgi.server.member.entity.type.MemberRole
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtProvider(

    @Value("\${jwt.secret-key}") private val secretKey: String,
    @Value("\${jwt.access-token-expire-seconds}") private val accessTokenExpireSeconds: Long,

    private val redisTemplate: StringRedisTemplate,
) {

    private val log = KotlinLogging.logger {}

    private val accessTokenExpiry = Duration.ofSeconds(accessTokenExpireSeconds)
    private val refreshTokenExpiry = Duration.ofDays(30)

    fun generateAccessToken(memberId: Long, role: MemberRole): String {
        return Jwts.builder()
            .setSubject(memberId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiry.toMillis()))
            .claim("type", "access")
            .claim("role", role.name)
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
                is ExpiredJwtException -> log.warn { "토큰이 만료되었습니다. ${e.message}" }
                is UnsupportedJwtException -> log.warn { "지원되지 않는 토큰입니다. ${e.message}" }
                is MalformedJwtException -> log.warn { "형식이 잘못된 토큰입니다. ${e.message}" }
                is SecurityException -> log.warn { "유효하지 않은 서명입니다. ${e.message}" }
                is IllegalArgumentException -> log.warn { "토큰 클레임이 비어 있습니다. ${e.message}" }
                else -> log.warn { "알 수 없는 오류가 발생했습니다. ${e.message}" }
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

    fun extractRole(token: String): MemberRole {
        val payload = getPayload(token)
        val role = payload["role"] as? String
            ?: throw IllegalArgumentException("토큰에 역할이 존재하지 않습니다.")

        return MemberRole.valueOf(role)
    }

    private fun getPayload(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
    }
}