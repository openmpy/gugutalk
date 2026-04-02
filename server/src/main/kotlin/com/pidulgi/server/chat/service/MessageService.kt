package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.event.MessageSendEvent
import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.common.exception.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(

    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @Transactional
    fun send(senderId: Long, chatRoomId: Long, request: MessageSendRequest) {
        val chatRoom = (chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다."))

        if (chatRoom.member1Id != senderId && chatRoom.member2Id != senderId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        // 메시지 저장
        val message = Message(
            chatRoom = chatRoom,
            senderId = senderId,
            content = request.content,
            type = request.type,
        )
        messageRepository.save(message)

        // 메시지 이벤트 전송
        val event = MessageSendEvent(
            message.id,
            senderId,
            request.content,
            request.type,
            message.createdAt,
        )
        messagingTemplate.convertAndSend(
            "/topic/chat-rooms/${chatRoomId}",
            event
        )
    }
}