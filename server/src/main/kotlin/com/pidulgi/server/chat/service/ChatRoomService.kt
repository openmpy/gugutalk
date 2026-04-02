package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomDeleteEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.entity.ChatRoom
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
class ChatRoomService(

    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional
    fun create(
        senderId: Long,
        targetId: Long,
    ): ChatRoomCreateResponse {
        memberRepository.findByIdOrNull(targetId)
            ?: throw CustomException("존재하지 않는 회원입니다.")

        val chatRoom = findChatRoom(senderId, targetId)
            ?: chatRoomRepository.save(ChatRoom.of(senderId, targetId))

        return ChatRoomCreateResponse(chatRoom.id)
    }

    @Transactional
    fun delete(
        memberId: Long,
        chatRoomId: Long
    ) {
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

        chatRoom.delete()

        // 방 구독 전체 전송
        val event = ChatEvent(
            DELETE_CHAT_ROOM,
            null
        )
        messagingTemplate.convertAndSend(
            "/topic/chat-rooms/${chatRoomId}",
            event
        )

        // 채널 구독 개인 전송
        val chatRoomEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            ChatRoomDeleteEvent(
                chatRoomId,
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
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): CursorResponse<ChatRoomGetResponse> {
        val result = chatRoomRepository.findChatRoomsByCursor(
            memberId,
            cursorId,
            cursorDate,
            size + 1
        ).map {
            ChatRoomGetResponse(
                chatRoomId = it.chatRoomId,
                targetId = it.targetId,
                nickname = it.nickname,
                profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                lastMessage = it.lastMessage,
                lastMessageAt = it.lastMessageAt,
                sortAt = it.sortAt,
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result
        val last = items.lastOrNull()

        return CursorResponse(
            payload = items,
            nextId = last?.chatRoomId,
            nextDateAt = last?.sortAt,
            hasNext = hasNext
        )
    }

    @Transactional
    fun markAsRead(memberId: Long, chatRoomId: Long) {
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다.")

        val lastMessageId = messageRepository.findLastMessageId(chatRoomId)
            ?: return

        chatRoom.read(memberId, lastMessageId)
    }

    private fun findChatRoom(memberA: Long, memberB: Long): ChatRoom? {
        val (m1, m2) = if (memberA < memberB) {
            memberA to memberB
        } else {
            memberB to memberA
        }
        return chatRoomRepository.findByMember1IdAndMember2Id(m1, m2)
    }
}