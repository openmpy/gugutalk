package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.dto.response.MessageSendResponse
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.ChatRoomMemberRepository
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.common.exception.CustomException
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(

    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageTemplate: SimpMessagingTemplate,
) {

    @Transactional
    fun send(senderId: Long, chatRoomId: Long, request: MessageSendRequest) {
        chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, senderId)
            ?: throw CustomException("채팅방에 접근할 수 없습니다.")

        val message = Message(
            chatRoomId = chatRoomId,
            senderId = senderId,
            content = request.content,
            type = request.type,
        )
        messageRepository.save(message)
        chatRoomRepository.updateLastMessage(chatRoomId, message.id, message.createdAt)

        val response = MessageSendResponse(
            messageId = message.id,
            chatRoomId = chatRoomId,
            senderId = senderId,
            content = request.content,
            type = message.type,
            createdAt = message.createdAt
        )
        messageTemplate.convertAndSend("/topic/chat/$chatRoomId", response)
    }
}