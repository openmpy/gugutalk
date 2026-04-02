package com.pidulgi.server.common.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

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
            val accessToken = (accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
                ?.removePrefix(AUTHORIZATION_HEADER_PREFIX)
                ?: throw ResponseStatusException(HttpStatus.FORBIDDEN))

            val memberId = jwtProvider.extractMemberId(accessToken)
            accessor.setUser { memberId.toString() }
        }
        return message
    }
}