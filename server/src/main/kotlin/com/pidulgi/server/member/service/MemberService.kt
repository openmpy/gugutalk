package com.pidulgi.server.member.service

import com.pidulgi.server.auth.service.AUTH_ACCESS_TOKEN_BLACKLIST_KEY
import com.pidulgi.server.auth.service.AUTH_REFRESH_TOKEN_KEY
import com.pidulgi.server.common.auth.ACCESS_TOKEN_EXPIRE_HOURS
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.PresignedUrlsResponse
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.dto.request.MemberGetPresignedUrlsRequest
import com.pidulgi.server.member.dto.request.MemberUpdateLocationRequest
import com.pidulgi.server.member.dto.request.MemberWithdrawRequest
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.util.*

@Service
class MemberService(

    private val memberRepository: MemberRepository,
    private val s3Service: S3Service,
    private val redisTemplate: StringRedisTemplate,
) {

    @Transactional(readOnly = true)
    fun getMe(memberId: Long): MemberGetMeResponse {
        val member = getMember(memberId)

        return MemberGetMeResponse(
            member.id,
            emptyList(),
            member.nickname,
            member.gender,
            LocalDate.now().year - member.birthYear,
            member.bio,
            0
        )
    }

    @Transactional
    fun withdraw(memberId: Long, request: MemberWithdrawRequest) {
        val member = getMember(memberId)

        val accessTokenBlacklist = AUTH_ACCESS_TOKEN_BLACKLIST_KEY + request.accessToken
        val refreshToken = AUTH_REFRESH_TOKEN_KEY + request.refreshToken

        redisTemplate.opsForValue().set(
            accessTokenBlacklist,
            memberId.toString(),
            Duration.ofHours(ACCESS_TOKEN_EXPIRE_HOURS)
        )
        redisTemplate.delete(refreshToken)

        member.withdraw()
    }

    @Transactional
    fun bump(memberId: Long) {
        val member = getMember(memberId)
        member.bump()
    }

    @Transactional
    fun updateLocation(memberId: Long, request: MemberUpdateLocationRequest) {
        val member = getMember(memberId)
        member.updateLocation(request.latitude, request.longitude)
    }

    @Transactional(readOnly = true)
    fun getPresignedUrls(
        memberId: Long,
        request: MemberGetPresignedUrlsRequest
    ): PresignedUrlsResponse {
        val member = getMember(memberId)

        val urls = request.images.map {
            val extension = it.contentType.substringAfterLast("/").lowercase()
            val imageTypeLowerCase = it.imageType.name.lowercase()
            val key = "members/${member.id}/$imageTypeLowerCase/${UUID.randomUUID()}.$extension"

            s3Service.createPresignedUrl(key, it.contentType)
        }
        return PresignedUrlsResponse(presigned = urls)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}