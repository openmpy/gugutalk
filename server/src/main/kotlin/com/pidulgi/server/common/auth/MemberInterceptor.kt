package com.pidulgi.server.common.auth

import com.pidulgi.server.ban.entity.Ban
import com.pidulgi.server.ban.repository.BanRepository
import com.pidulgi.server.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor
import java.time.format.DateTimeFormatter

class MemberInterceptor(

    private val memberRepository: MemberRepository,
    private val banRepository: BanRepository,
    private val jwtProvider: JwtProvider,
) : HandlerInterceptor {

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        private const val SUPPORT_EMAIL = "gugutalk@proton.me"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val deviceId = request.getHeader("X-Device-Id")
        if (deviceId != null) {
            banRepository.findByUuid(deviceId)?.let { throwBanException(it) }
        }

        val accessToken = AuthenticationExtractor.extract(request) ?: return true
        if (!jwtProvider.validateToken(accessToken)) return true

        val memberId = jwtProvider.extractMemberId(accessToken)
        val member = memberRepository.findByIdOrNull(memberId) ?: return true

        banRepository.findByUuid(member.uuid.value)?.let {
            throwBanException(it)
        }
        banRepository.findByPhoneNumber(member.phoneNumber.value)?.let {
            throwBanException(it)
        }
        return true
    }

    private fun throwBanException(ban: Ban): Nothing {
        throw ResponseStatusException(
            HttpStatus.LOCKED,
            """
                번호: ${ban.uuid}
                유형: ${ban.type.text}
                사유: ${ban.reason ?: "-"}
                해제일: ${ban.expiredAt.format(DATE_FORMATTER)}
                
                문의: $SUPPORT_EMAIL
            """.trimIndent()
        )
    }
}