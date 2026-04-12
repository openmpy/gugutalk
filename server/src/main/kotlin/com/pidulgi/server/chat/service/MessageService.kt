package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomSendEvent
import com.pidulgi.server.chat.dto.event.MessageSendEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.SEND_CHAT_ROOM
import com.pidulgi.server.chat.dto.event.type.ChatEventType.SEND_MESSAGE
import com.pidulgi.server.chat.dto.request.MessageSendMediaRequest
import com.pidulgi.server.chat.dto.request.MessageSendRequest
import com.pidulgi.server.chat.dto.response.MessageGetMemberResponse
import com.pidulgi.server.chat.dto.response.MessageGetResponse
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.entity.type.MessageType
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.chat.service.event.ChatQueueEvent
import com.pidulgi.server.chat.service.event.ChatTopicEvent
import com.pidulgi.server.chat.websocket.ChatRoomSessionManager
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.social.repository.BlockRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MessageService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val memberRepository: MemberRepository,
    private val blockRepository: BlockRepository,
    private val chatRoomSessionManager: ChatRoomSessionManager,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun send(senderId: Long, chatRoomId: Long, request: MessageSendRequest) {
        val sender = getMember(senderId)
        val chatRoom = getChatRoom(chatRoomId)

        check(chatRoom.hasMember(senderId)) { "접근할 수 없는 채팅방입니다." }

        val targetId = getTargetId(chatRoom, senderId)

        check(!blockRepository.existsBlock(senderId, targetId)) { "차단된 회원입니다." }

        // 메시지 저장
        val message = Message(
            chatRoom = chatRoom,
            senderId = senderId,
            content = request.content,
            type = MessageType.TEXT,
        )
        messageRepository.save(message)

        // 채팅방 업데이트
        chatRoom.update(message.content, message.createdAt)

        // 상대가 방에 없을 때만 unread 증가
        val isActive = chatRoomSessionManager.isInChatRoom(targetId, chatRoomId)
        if (!isActive) {
            chatRoomRepository.increaseUnreadCount(chatRoomId, targetId)
        }

        // unreadCount 계산
        val updatedUnreadCount = if (isActive) 0 else chatRoom.getUnreadCount(targetId) + 1

        // 이벤트
        val messageEvent = ChatEvent(
            SEND_MESSAGE,
            MessageSendEvent(
                message.id,
                senderId,
                request.content,
                MessageType.TEXT,
                message.createdAt,
            )
        )
        applicationEventPublisher.publishEvent(ChatTopicEvent(chatRoomId, messageEvent))

        val chatRoomEvent = ChatEvent(
            SEND_CHAT_ROOM,
            ChatRoomSendEvent(
                chatRoomId,
                senderId,
                sender.profileKey?.let { key -> "$endpoint$key" },
                sender.nickname.value,
                chatRoom.lastMessage,
                message.type,
                chatRoom.lastMessageAt,
                updatedUnreadCount
            )
        )
        applicationEventPublisher.publishEvent(ChatQueueEvent(targetId, chatRoomEvent))
    }

    @Transactional
    fun sendMedia(senderId: Long, chatRoomId: Long, request: MessageSendMediaRequest) {
        val sender = getMember(senderId)
        val chatRoom = getChatRoom(chatRoomId)

        check(chatRoom.hasMember(senderId)) { "접근할 수 없는 채팅방입니다." }

        val targetId = getTargetId(chatRoom, senderId)

        check(!blockRepository.existsBlock(senderId, targetId)) { "차단된 회원입니다." }

        val isActive = chatRoomSessionManager.isInChatRoom(targetId, chatRoomId)

        val mediaKeys = (request.imageKeys.map { it to MessageType.IMAGE }) +
                (request.videoKeys.map { it to MessageType.VIDEO })
        for ((key, type) in mediaKeys) {
            sendMediaMessage(chatRoom, sender, senderId, targetId, chatRoomId, key, type, isActive)
        }
    }

    @Transactional(readOnly = true)
    fun gets(
        memberId: Long,
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): CursorResponse<MessageGetResponse> {
        val chatRoom = getChatRoom(chatRoomId)

        check(chatRoom.hasMember(memberId)) { "접근할 수 없는 채팅방입니다." }

        val result = messageRepository.findMessagesByCursor(
            chatRoomId,
            cursorId,
            cursorDate,
            size + 1
        ).map {
            val content = when (it.type) {
                MessageType.IMAGE, MessageType.VIDEO -> "$endpoint${it.content}"
                else -> it.content
            }
            MessageGetResponse(
                it.messageId,
                it.senderId,
                content,
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
        val chatRoom = getChatRoom(chatRoomId)

        check(chatRoom.hasMember(memberId)) { "접근할 수 없는 채팅방입니다." }

        val targetId = getTargetId(chatRoom, memberId)
        val target = getMember(targetId)

        return MessageGetMemberResponse(
            target.id,
            target.profileKey?.let { key -> "$endpoint$key" },
            target.nickname.value,
        )
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))

    private fun getChatRoom(chatRoomId: Long): ChatRoom =
        (chatRoomRepository.findByIdOrNull(chatRoomId) ?: throw CustomException("존재하지 않는 채팅방입니다."))

    private fun getTargetId(chatRoom: ChatRoom, senderId: Long): Long = if (chatRoom.member1Id == senderId) {
        chatRoom.member2Id
    } else chatRoom.member1Id

    private fun sendMediaMessage(
        chatRoom: ChatRoom,
        sender: Member,
        senderId: Long,
        targetId: Long,
        chatRoomId: Long,
        key: String,
        type: MessageType,
        isActive: Boolean,
    ) {
        val label = if (type == MessageType.IMAGE) "이미지" else "동영상"

        val message = messageRepository.save(
            Message(chatRoom = chatRoom, senderId = senderId, content = key, type = type)
        )
        chatRoom.update(label, message.createdAt)

        if (!isActive) chatRoomRepository.increaseUnreadCount(chatRoomId, targetId)

        val updatedUnreadCount = if (isActive) 0 else chatRoom.getUnreadCount(targetId) + 1

        // 이벤트
        val messageEvent = ChatEvent(
            SEND_MESSAGE,
            MessageSendEvent(
                message.id,
                senderId,
                "$endpoint$key",
                type,
                message.createdAt
            )
        )
        applicationEventPublisher.publishEvent(ChatTopicEvent(chatRoomId, messageEvent))

        val chatRoomEvent = ChatEvent(
            SEND_CHAT_ROOM,
            ChatRoomSendEvent(
                chatRoomId,
                senderId,
                sender.profileKey?.let { "$endpoint$it" },
                sender.nickname.value,
                label,
                type,
                message.createdAt,
                updatedUnreadCount
            )
        )
        applicationEventPublisher.publishEvent(ChatQueueEvent(targetId, chatRoomEvent))
    }
}