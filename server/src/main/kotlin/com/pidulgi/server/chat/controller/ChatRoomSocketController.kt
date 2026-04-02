package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.websocket.ChatRoomSessionManager
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class ChatRoomSocketController(

    private val chatRoomSessionManager: ChatRoomSessionManager
) {

    @MessageMapping("/chat-rooms/{chatRoomId}/enter")
    fun enter(
        @DestinationVariable chatRoomId: Long,
        principal: Principal
    ) {
        val memberId = principal.name.toLong()
        chatRoomSessionManager.enter(memberId, chatRoomId)

        println("[입장] 회원 번호 $memberId")
    }

    @MessageMapping("/chat-rooms/leave")
    fun leave(
        principal: Principal
    ) {
        val memberId = principal.name.toLong()
        chatRoomSessionManager.leave(memberId)

        println("[퇴장] 회원 번호 $memberId")
    }
}