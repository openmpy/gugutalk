package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.service.MessageService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class ChatStompController(

    private val messageService: MessageService,
) {

    @MessageMapping("/chat/{chatRoomId}")
    fun sendMessage(
        principal: Principal,
        @DestinationVariable chatRoomId: Long,
        @Payload request: MessageSendRequest,
    ) {
        val senderId = principal.name.toLong()
        messageService.send(senderId, chatRoomId, request)
    }
}