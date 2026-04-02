package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomSendEvent
import com.pidulgi.server.chat.dto.event.MessageSendEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.SEND_CHAT_ROOM
import com.pidulgi.server.chat.dto.event.type.ChatEventType.SEND_MESSAGE
import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.dto.response.MessageGetMemberResponse
import com.pidulgi.server.chat.dto.response.MessageGetResponse
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MessageService(

    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val memberRepository: MemberRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional
    fun send(senderId: Long, chatRoomId: Long, request: MessageSendRequest) {
        val sender = (memberRepository.findByIdOrNull(senderId)
            ?: throw CustomException("존재하지 않는 회원입니다."))
        val chatRoom = (chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다."))

        if (chatRoom.member1Id != senderId && chatRoom.member2Id != senderId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        val targetId = if (chatRoom.member1Id == senderId) {
            chatRoom.member2Id
        } else {
            chatRoom.member1Id
        }

        val message = Message(
            chatRoom = chatRoom,
            senderId = senderId,
            content = request.content,
            type = request.type,
        )
        messageRepository.save(message)

        chatRoom.update(message.content, message.createdAt)
        chatRoomRepository.increaseUnreadCount(chatRoomId, targetId)
        val updatedUnreadCount = if (targetId == chatRoom.member1Id) {
            chatRoom.member1UnreadCount + 1
        } else chatRoom.member2UnreadCount + 1

        // 방 구독 전체 전송
        val event = ChatEvent(
            SEND_MESSAGE,
            MessageSendEvent(
                message.id,
                senderId,
                request.content,
                request.type,
                message.createdAt,
            )
        )
        messagingTemplate.convertAndSend(
            "/topic/chat-rooms/${chatRoomId}",
            event
        )

        // 채널 구독 개인 전송
        val chatRoomEvent = ChatEvent(
            SEND_CHAT_ROOM,
            ChatRoomSendEvent(
                chatRoomId,
                senderId,
                sender.profileKey?.let { key -> "$endpoint$key" },
                sender.nickname,
                chatRoom.lastMessage,
                message.type,
                chatRoom.lastMessageAt,
                updatedUnreadCount,
            )
        )
        messagingTemplate.convertAndSendToUser(
            targetId.toString(),
            "/queue/chat-rooms",
            chatRoomEvent
        )
    }

    @Transactional(readOnly = true)
    fun gets(
        memberId: Long,
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): CursorResponse<MessageGetResponse> {
        val chatRoom = (chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다."))

        if (chatRoom.member1Id != memberId && chatRoom.member2Id != memberId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        val result = messageRepository.findMessagesByCursor(
            chatRoomId,
            cursorId,
            cursorDate,
            size + 1
        ).map {
            MessageGetResponse(
                it.messageId,
                it.senderId,
                it.content,
                it.type,
                it.createdAt,
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result
        val last = items.lastOrNull()

        return CursorResponse(
            payload = items,
            nextId = last?.messageId,
            nextDateAt = last?.createdAt,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun getMember(memberId: Long, chatRoomId: Long): MessageGetMemberResponse {
        val chatRoom = (chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다."))

        if (chatRoom.member1Id != memberId && chatRoom.member2Id != memberId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        val targetId = if (chatRoom.member1Id == memberId) {
            chatRoom.member2Id
        } else {
            chatRoom.member1Id
        }

        val target = (memberRepository.findByIdOrNull(targetId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        return MessageGetMemberResponse(
            target.id,
            target.profileKey?.let { key -> "$endpoint$key" },
            target.nickname,
        )
    }
}