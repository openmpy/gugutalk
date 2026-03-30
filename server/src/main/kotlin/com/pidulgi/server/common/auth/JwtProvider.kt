package com.pidulgi.server.common.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtProvider {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private val accessTokenExpiry = Duration.ofHours(1)
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
}