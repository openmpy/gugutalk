package com.pidulgi.server.auth.service

import com.pidulgi.server.auth.dto.request.ActivateRequest
import com.pidulgi.server.auth.dto.request.LoginRequest
import com.pidulgi.server.auth.dto.request.SignupRequest
import com.pidulgi.server.auth.dto.request.ValidateRequest
import com.pidulgi.server.auth.dto.response.LoginResponse
import com.pidulgi.server.auth.dto.response.SignupResponse
import com.pidulgi.server.auth.entity.PhoneVerification
import com.pidulgi.server.auth.repository.PhoneVerificationRepository
import com.pidulgi.server.common.auth.ACCESS_TOKEN_EXPIRE_HOURS
import com.pidulgi.server.common.auth.AuthenticationExtractor
import com.pidulgi.server.common.auth.JwtProvider
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.util.ClientIpExtractor
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDate
import java.util.*

private const val AUTH_VERIFICATION_CODE_KEY = "auth:phone:code:"
const val AUTH_REFRESH_TOKEN_KEY = "auth:refresh-token:"
const val AUTH_ACCESS_TOKEN_BLACKLIST_KEY = "auth:access-token:blacklist:"

private const val AUTH_VERIFICATION_CODE_MINUTES: Long = 5

@Service
class AuthService(

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val phoneVerificationRepository: PhoneVerificationRepository,
    private val redisTemplate: StringRedisTemplate,
    private val jwtProvider: JwtProvider,
) {

    @Transactional
    fun sendVerificationCode(servletRequest: HttpServletRequest, phoneNumber: String) {
        val key = AUTH_VERIFICATION_CODE_KEY + phoneNumber

        redisTemplate.opsForValue().get(key)?.let {
            throw CustomException("인증 번호가 이미 전송되었습니다.")
        }

        val verificationCode = "12345"
        redisTemplate.opsForValue().set(
            key,
            verificationCode,
            Duration.ofMinutes(AUTH_VERIFICATION_CODE_MINUTES)
        )

        if (!memberRepository.existsByPhoneNumber(phoneNumber)) {
            // SMS 인증 번호 전송

            // 인증 번호 기록 저장
            val verification = PhoneVerification(
                phoneNumber = phoneNumber,
                verificationCode = verificationCode,
                clientIp = ClientIpExtractor.extract(servletRequest)
            )
            phoneVerificationRepository.save(verification)
        }
    }

    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        val key = AUTH_VERIFICATION_CODE_KEY + request.phoneNumber
        val value = redisTemplate.opsForValue().get(key)

        value ?: throw CustomException("존재하지 않는 인증 번호입니다.")

        if (value != request.verificationCode) {
            throw CustomException("인증 번호가 일치하지 않습니다.")
        }
        if (memberRepository.existsByPhoneNumber(request.phoneNumber)) {
            throw CustomException("이미 가입된 휴대폰 번호입니다.")
        }

        val member = Member(
            uuid = request.uuid,
            phoneNumber = request.phoneNumber,
            password = request.password,
            nickname = UUID.randomUUID().toString().replace("-", "").substring(0, 10),
            gender = request.gender,
        )
        memberRepository.save(member)

        val accessToken = jwtProvider.generateAccessToken(member.id)
        val refreshToken = jwtProvider.generateRefreshToken(member.id)
        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken

        redisTemplate.opsForValue().set(refreshTokenKey, member.id.toString())
        redisTemplate.delete(key)

        return SignupResponse(
            member.id,
            accessToken,
            refreshToken,
        )
    }

    @Transactional
    fun activate(memberId: Long, request: ActivateRequest) {
        val member = getMember(memberId)

        if (memberRepository.existsByNickname(request.nickname)) {
            throw CustomException("이미 가입된 닉네임입니다.")
        }

        val memberImages = request.images.map {
            MemberImage(
                memberId = member.id,
                key = it.key,
                type = ImageType.PUBLIC,
                sortOrder = it.index
            )
        }
        memberImageRepository.saveAll(memberImages)

        val profileKey = request.images.minByOrNull { it.index }?.key
        member.activate(
            profileKey,
            request.nickname,
            request.birthYear,
            request.bio
        )
    }

    @Transactional(readOnly = true)
    fun validate(request: ValidateRequest) {
        if (memberRepository.existsByNickname(request.nickname)) {
            throw CustomException("이미 가입된 닉네임입니다.")
        }
        if (LocalDate.now().year - request.birthYear < 19 || LocalDate.now().year - request.birthYear > 60) {
            throw CustomException("만 19세 이상 60세 이하만 가입할 수 있습니다.")
        }
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val member = (memberRepository.findByPhoneNumber(request.phoneNumber)
            ?: throw CustomException("다시 한번 확인해주시길 바랍니다."))

        if (member.password != request.password) {
            throw CustomException("다시 한번 확인해주시길 바랍니다.")
        }

        val accessToken = jwtProvider.generateAccessToken(member.id)
        val refreshToken = jwtProvider.generateRefreshToken(member.id)
        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken

        redisTemplate.opsForValue().set(refreshTokenKey, member.id.toString())

        return LoginResponse(
            member.id,
            accessToken,
            refreshToken,
        )
    }

    @Transactional
    fun logout(servletRequest: HttpServletRequest, refreshToken: String) {
        val accessToken = AuthenticationExtractor.extract(servletRequest)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val accessTokenBlacklist = AUTH_ACCESS_TOKEN_BLACKLIST_KEY + accessToken
        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken

        redisTemplate.opsForValue().set(
            accessTokenBlacklist,
            "1",
            Duration.ofHours(ACCESS_TOKEN_EXPIRE_HOURS)
        )
        redisTemplate.delete(refreshTokenKey)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}