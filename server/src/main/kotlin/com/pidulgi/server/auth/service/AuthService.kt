package com.pidulgi.server.auth.service

import com.pidulgi.server.auth.dto.request.*
import com.pidulgi.server.auth.dto.response.LoginResponse
import com.pidulgi.server.auth.dto.response.RotateTokenResponse
import com.pidulgi.server.auth.dto.response.SignupResponse
import com.pidulgi.server.auth.entity.PhoneVerification
import com.pidulgi.server.auth.repository.PhoneVerificationRepository
import com.pidulgi.server.common.auth.AuthenticationExtractor
import com.pidulgi.server.common.auth.JwtProvider
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.sms.SmsSender
import com.pidulgi.server.common.util.ClientIpExtractor
import com.pidulgi.server.common.util.NumberGenerator
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.entity.vo.MemberNickname
import com.pidulgi.server.member.entity.vo.MemberPassword
import com.pidulgi.server.member.entity.vo.MemberPhoneNumber
import com.pidulgi.server.member.entity.vo.MemberUuid
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.point.entity.Point
import com.pidulgi.server.point.repository.PointRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

const val AUTH_REFRESH_TOKEN_KEY = "auth:refresh-token:"
const val AUTH_ACCESS_TOKEN_BLACKLIST_KEY = "auth:access-token:blacklist:"

@Service
class AuthService(
    @Value("\${jwt.access-token-expire-seconds}") private val accessTokenExpireSeconds: Long,

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val phoneVerificationRepository: PhoneVerificationRepository,
    private val pointRepository: PointRepository,
    private val redisTemplate: StringRedisTemplate,
    private val jwtProvider: JwtProvider,
    private val smsSender: SmsSender,
) {

    companion object {
        private const val AUTH_SMS_IP_DAILY_LIMIT_KEY = "auth:sms:ip:daily:"
        private const val AUTH_SMS_VERIFICATION_CODE_KEY = "auth:sms:code:"

        private const val AUTH_SMS_MAX_DAILY_SEND_COUNT = 3
        private const val AUTH_VERIFICATION_CODE_MINUTES = 5L
    }

    @Transactional
    fun sendVerificationCode(servletRequest: HttpServletRequest, phoneNumber: String) {
        val clientIp = ClientIpExtractor.extract(servletRequest)
        val memberPhoneNumber = MemberPhoneNumber(phoneNumber)

        // 인증 번호 전송 횟수 검사
        val ipLimitKey = AUTH_SMS_IP_DAILY_LIMIT_KEY + clientIp
        val sendCount = redisTemplate.opsForValue().get(ipLimitKey)?.toIntOrNull() ?: 0

        if (sendCount >= AUTH_SMS_MAX_DAILY_SEND_COUNT) {
            throw CustomException("하루 최대 ${AUTH_SMS_MAX_DAILY_SEND_COUNT}회까지만 전송할 수 있습니다.")
        }

        // 인증 번호 생성
        val verificationCodeKey = AUTH_SMS_VERIFICATION_CODE_KEY + memberPhoneNumber.value
        redisTemplate.opsForValue().get(verificationCodeKey)?.let {
            throw CustomException("인증 번호가 이미 전송되었습니다.")
        }

        val verificationCode = NumberGenerator.generate()
        redisTemplate.opsForValue().set(
            verificationCodeKey,
            verificationCode,
            Duration.ofMinutes(AUTH_VERIFICATION_CODE_MINUTES)
        )

        // 인증 번호 전송
        if (!memberRepository.existsByPhoneNumber(memberPhoneNumber)) {
            smsSender.send(phoneNumber, "구구톡 인증 번호는 [${verificationCode}]입니다.")

            val verification = PhoneVerification(
                phoneNumber = memberPhoneNumber,
                verificationCode = verificationCode,
                clientIp = clientIp
            )
            phoneVerificationRepository.save(verification)
        }

        // 인증 번호 전송 횟수 카운트
        val midnight = LocalDate.now().plusDays(1).atStartOfDay()
        val secondsUntilMidnight = Duration.between(LocalDateTime.now(), midnight)
        val count = redisTemplate.opsForValue().increment(ipLimitKey)

        if (count == 1L) {
            redisTemplate.expire(ipLimitKey, secondsUntilMidnight)
        }
    }

    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        val key = AUTH_SMS_VERIFICATION_CODE_KEY + request.phoneNumber
        val value = redisTemplate.opsForValue().get(key)

        value ?: throw CustomException("존재하지 않는 인증 번호입니다.")

        if (value != request.verificationCode) {
            throw CustomException("인증 번호가 일치하지 않습니다.")
        }
        if (memberRepository.existsByPhoneNumber(MemberPhoneNumber(request.phoneNumber))) {
            throw CustomException("이미 가입된 휴대폰 번호입니다.")
        }

        val member = Member(
            uuid = MemberUuid(request.uuid),
            phoneNumber = MemberPhoneNumber(request.phoneNumber),
            password = MemberPassword(request.password),
            nickname = MemberNickname(UUID.randomUUID().toString().replace("-", "").substring(0, 10)),
            gender = request.gender,
        )
        memberRepository.save(member)

        val point = Point(memberId = member.id)
        pointRepository.save(point)

        val accessToken = jwtProvider.generateAccessToken(member.id, member.role)
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

        if (member.nickname.value != request.nickname && memberRepository.existsByNickname(request.nickname)) {
            throw CustomException("이미 사용 중인 닉네임입니다.")
        }
        if (LocalDate.now().year - request.birthYear !in 19..60) {
            throw CustomException("만 19세 이상 60세 이하만 가입할 수 있습니다.")
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
    fun validate(memberId: Long, request: ValidateRequest) {
        val member = getMember(memberId)

        if (member.nickname.value != request.nickname && memberRepository.existsByNickname(request.nickname)) {
            throw CustomException("이미 사용 중인 닉네임입니다.")
        }
        if (LocalDate.now().year - request.birthYear !in 19..60) {
            throw CustomException("만 19세 이상 60세 이하만 가입할 수 있습니다.")
        }
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val member = (memberRepository.findByPhoneNumber(request.phoneNumber)
            ?: throw CustomException("다시 한번 확인해주시길 바랍니다."))

        if (member.password.value != request.password) {
            throw CustomException("다시 한번 확인해주시길 바랍니다.")
        }

        val accessToken = jwtProvider.generateAccessToken(member.id, member.role)
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
            Duration.ofSeconds(accessTokenExpireSeconds)
        )
        redisTemplate.delete(refreshTokenKey)
    }

    @Transactional
    fun rotateToken(request: RotateTokenRequest): RotateTokenResponse {
        val member = getMember(request.memberId)

        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + request.refreshToken
        val exists = redisTemplate.hasKey(refreshTokenKey)

        if (exists == false) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "존재하지 않는 리프레시 토큰입니다.")
        }

        val accessToken = jwtProvider.generateAccessToken(member.id, member.role)
        val refreshToken = jwtProvider.generateRefreshToken(member.id)
        val newRefreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken

        redisTemplate.opsForValue().set(newRefreshTokenKey, member.id.toString())
        redisTemplate.delete(refreshTokenKey)

        return RotateTokenResponse(
            accessToken,
            refreshToken,
        )
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}