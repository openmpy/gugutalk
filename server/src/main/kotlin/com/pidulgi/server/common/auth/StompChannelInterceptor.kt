package com.pidulgi.server.common.auth

import org.springframework.http.HttpHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessagingException
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class StompChannelInterceptor(

    private val jwtProvider: JwtProvider,
) : ChannelInterceptor {

    override fun preSend(
        message: Message<*>,
        channel: MessageChannel
    ): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        if (accessor.command == StompCommand.CONNECT) {
            val header = (accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
                ?: throw MessagingException("인증 헤더 값을 찾을 수 없습니다."))

            if (!header.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
                throw MessagingException("유효하지 않은 토큰 형식입니다.")
            }

            val accessToken = header.removePrefix(AUTHORIZATION_HEADER_PREFIX)
            val memberId = jwtProvider.extractMemberId(accessToken)

            accessor.setUser { memberId.toString() }
        }
        return message
    }
}