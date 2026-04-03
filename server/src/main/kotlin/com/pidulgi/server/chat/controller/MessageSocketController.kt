package com.pidulgi.server.chat.controller

import com.pidulgi.server.chat.dto.request.MessageSendMediaRequest
import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.service.MessageService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class MessageSocketController(

    private val messageService: MessageService,
) {

    @MessageMapping("/chat-rooms/{chatRoomId}/messages")
    fun sendMessage(
        @DestinationVariable chatRoomId: Long,
        @Payload request: MessageSendRequest,
        principal: Principal,
    ) {
        val senderId = principal.name.toLong()
        messageService.send(senderId, chatRoomId, request)
    }

    @MessageMapping("/chat-rooms/{chatRoomId}/medias")
    fun sendMedia(
        @DestinationVariable chatRoomId: Long,
        @Payload request: MessageSendMediaRequest,
        principal: Principal,
    ) {
        val senderId = principal.name.toLong()
        messageService.sendMedia(senderId, chatRoomId, request)
    }
}