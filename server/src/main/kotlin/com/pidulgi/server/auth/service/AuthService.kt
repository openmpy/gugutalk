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
import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.common.util.ClientIpExtractor
import com.pidulgi.server.common.util.NumberGenerator
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.entity.vo.*
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.point.entity.Point
import com.pidulgi.server.point.repository.PointRepository
import jakarta.servlet.http.HttpServletRequest
import org.apache.hc.client5.http.auth.InvalidCredentialsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

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

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

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

        // 인증 번호 전송
        if (!memberRepository.existsByPhoneNumber(memberPhoneNumber)) {
            val verificationCode = NumberGenerator.generate()
            smsSender.send(phoneNumber, "구구톡 인증 번호는 [${verificationCode}]입니다.")

            redisTemplate.opsForValue().set(
                verificationCodeKey,
                verificationCode,
                Duration.ofMinutes(AUTH_VERIFICATION_CODE_MINUTES)
            )

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
        val memberPhoneNumber = MemberPhoneNumber(request.phoneNumber)
        val gender = Gender.from(request.gender)

        // 인증 번호 검증
        val verificationCodeKey = AUTH_SMS_VERIFICATION_CODE_KEY + request.phoneNumber
        val verificationCode = redisTemplate.opsForValue().get(verificationCodeKey)
            ?: throw CustomException("존재하지 않는 인증 번호입니다.")
        if (verificationCode != request.verificationCode) {
            throw CustomException("인증 번호가 일치하지 않습니다.")
        }

        // 회원 가입 검증
        if (memberRepository.existsByPhoneNumber(memberPhoneNumber)) {
            throw CustomException("이미 가입된 휴대폰 번호입니다.")
        }

        // 회원 계정 생성
        val password = passwordEncoder.encode(request.password)
            ?: throw CustomException("비밀번호 암호화에 실패했습니다.")

        val member = Member(
            uuid = MemberUuid(request.uuid),
            phoneNumber = memberPhoneNumber,
            password = MemberPassword(password),
            gender = gender,
        )
        memberRepository.save(member)

        // 포인트 정보 생성
        val point = Point(memberId = member.id)
        pointRepository.save(point)

        // 토큰 발급
        val accessToken = jwtProvider.generateAccessToken(member.id, member.role)
        val refreshToken = jwtProvider.generateRefreshToken(member.id)
        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken

        redisTemplate.opsForValue().set(refreshTokenKey, member.id.toString())
        redisTemplate.delete(verificationCodeKey)

        return SignupResponse(
            member.id,
            accessToken,
            refreshToken,
        )
    }

    @Transactional(readOnly = true)
    fun validate(memberId: Long, request: ValidateRequest) {
        val memberNickname = MemberNickname(request.nickname)
        if (AgeCalculator.calculate(request.birthYear) !in 20..60) {
            throw CustomException("20세 이상 60세 이하만 가입할 수 있습니다.")
        }

        val member = getMember(memberId)
        if (member.nickname.value != request.nickname && memberRepository.existsByNickname(memberNickname)) {
            throw CustomException("이미 사용 중인 닉네임입니다.")
        }
    }

    @Transactional
    fun activate(memberId: Long, request: ActivateRequest) {
        val memberNickname = MemberNickname(request.nickname)
        val memberBirthYear = MemberBirthYear(request.birthYear)
        val memberBio = request.bio?.let { MemberBio(it) }

        val member = getMember(memberId)
        if (member.nickname.value != request.nickname && memberRepository.existsByNickname(memberNickname)) {
            throw CustomException("이미 사용 중인 닉네임입니다.")
        }

        // 회원 프로필 이미지 저장
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
            memberNickname,
            memberBirthYear,
            memberBio
        )
    }

    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        val memberPhoneNumber = MemberPhoneNumber(request.phoneNumber)

        val member = (memberRepository.findByPhoneNumber(memberPhoneNumber)
            ?: throw CustomException("다시 한번 확인해주시길 바랍니다."))

        if (!passwordEncoder.matches(request.password, member.password.value)) {
            throw InvalidCredentialsException("다시 한번 확인해주시길 바랍니다.")
        }

        val accessToken = jwtProvider.generateAccessToken(member.id, member.role)
        val refreshToken = jwtProvider.generateRefreshToken(member.id)

        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken
        redisTemplate.opsForValue().set(refreshTokenKey, member.id.toString())

        member.updateUuid(MemberUuid(request.uuid))

        return LoginResponse(
            member.id,
            accessToken,
            refreshToken,
        )
    }

    @Transactional
    fun logout(servletRequest: HttpServletRequest, refreshToken: String) {
        val accessToken = AuthenticationExtractor.extract(servletRequest)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "액세스 토큰을 찾을 수 없습니다.")

        val accessTokenBlacklistKey = AUTH_ACCESS_TOKEN_BLACKLIST_KEY + accessToken
        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken

        redisTemplate.opsForValue().set(
            accessTokenBlacklistKey,
            "1",
            Duration.ofSeconds(accessTokenExpireSeconds)
        )
        redisTemplate.delete(refreshTokenKey)
    }

    @Transactional
    fun rotateToken(request: RotateTokenRequest): RotateTokenResponse {
        val member = getMember(request.memberId)

        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + request.refreshToken
        val refreshTokenKeyExists = redisTemplate.hasKey(refreshTokenKey)
        if (refreshTokenKeyExists == false) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "로그인을 다시 해주시길 바랍니다.")
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