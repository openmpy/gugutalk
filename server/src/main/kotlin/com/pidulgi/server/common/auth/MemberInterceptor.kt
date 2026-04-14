package com.pidulgi.server.common.auth

import com.pidulgi.server.ban.repository.BanRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor
import java.time.format.DateTimeFormatter

class MemberInterceptor(

    private val banRepository: BanRepository,
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val deviceId = request.getHeader("X-Device-Id")

        if (deviceId != null) {
            banRepository.findByUuid(deviceId)?.let {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

                throw ResponseStatusException(
                    HttpStatus.LOCKED,
                    """
                    번호: ${it.uuid}
                    유형: ${it.type.text}
                    사유: ${it.reason ?: "-"}
                    해제일: ${it.expiredAt.format(formatter)}
                    
                    문의: gugutalk@proton.me
                """.trimIndent()
                )
            }
        }
        return true
    }
}