package com.pidulgi.server.common.websocket

import com.pidulgi.server.chat.websocket.ChatRoomSessionManager
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener(

    private val chatRoomSessionManager: ChatRoomSessionManager
) {

    @EventListener
    fun handleDisconnect(event: SessionDisconnectEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val member = accessor.user ?: return
        val memberId = member.name.toLong()

        chatRoomSessionManager.leave(memberId)
    }
}