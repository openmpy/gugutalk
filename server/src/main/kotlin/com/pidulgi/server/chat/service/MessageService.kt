package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.dto.response.MessageGetResponse
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.ChatRoomMemberRepository
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MessageService(

    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageTemplate: SimpMessagingTemplate,
) {

    @Transactional
    fun send(senderId: Long, chatRoomId: Long, request: MessageSendRequest) {
        val members = chatRoomMemberRepository.findAllByChatRoomId(chatRoomId)
        members.firstOrNull { it.memberId == senderId }
            ?: throw IllegalStateException("채팅방에 접근할 수 없습니다.")
        val targetId = members.first { it.memberId != senderId }.memberId

        val message = Message(
            chatRoomId = chatRoomId,
            senderId = senderId,
            content = request.content,
            type = request.type,
        )
        messageRepository.save(message)
        chatRoomRepository.updateLastMessage(chatRoomId, message.id, message.createdAt)

        val response = MessageGetResponse(
            messageId = message.id,
            chatRoomId = chatRoomId,
            senderId = senderId,
            targetId = targetId,
            content = request.content,
            type = message.type,
            createdAt = message.createdAt
        )
        messageTemplate.convertAndSend("/topic/chat/$chatRoomId", response)
    }

    @Transactional(readOnly = true)
    fun gets(
        memberId: Long,
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): CursorResponse<MessageGetResponse> {
        chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId)
            ?: throw CustomException("채팅방에 접근할 수 없습니다.")

        val result = messageRepository.findAllChatRoomByCursor(
            memberId, chatRoomId, cursorId, cursorDate, size + 1
        ).map {
            MessageGetResponse(
                messageId = it.messageId,
                chatRoomId = it.chatRoomId,
                senderId = it.senderId,
                targetId = it.targetId,
                content = it.content,
                type = it.type,
                createdAt = it.createdAt
            )
        }
        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = if (hasNext) items.last().messageId else null,
            nextDateAt = if (hasNext) items.last().createdAt else null,
            hasNext = hasNext
        )
    }
}