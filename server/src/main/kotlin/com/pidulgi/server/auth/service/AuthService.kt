package com.pidulgi.server.auth.service

import com.pidulgi.server.auth.entity.PhoneVerification
import com.pidulgi.server.auth.repository.PhoneVerificationRepository
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.util.ClientIpExtractor
import com.pidulgi.server.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

private const val AUTH_VERIFICATION_CODE_KEY = "auth:verification-code:"
private const val AUTH_VERIFICATION_CODE_MINUTES: Long = 5

@Service
class AuthService(

    private val memberRepository: MemberRepository,
    private val phoneVerificationRepository: PhoneVerificationRepository,
    private val redisTemplate: StringRedisTemplate,
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
}