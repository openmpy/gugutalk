package com.pidulgi.server.chat.service

import com.google.firebase.messaging.*
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
import com.pidulgi.server.chat.entity.PREVIEW_MAX_LENGTH
import com.pidulgi.server.chat.entity.type.MessageType
import com.pidulgi.server.chat.entity.type.MessageType.*
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.chat.service.event.ChatQueueEvent
import com.pidulgi.server.chat.service.event.ChatTopicEvent
import com.pidulgi.server.chat.service.extension.toMessageGetResponse
import com.pidulgi.server.chat.service.query.GetsMessageQuery
import com.pidulgi.server.chat.websocket.ChatRoomSessionManager
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.fcm.repository.FcmTokenRepository
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.social.repository.BlockRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val memberRepository: MemberRepository,
    private val blockRepository: BlockRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    private val chatRoomSessionManager: ChatRoomSessionManager,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    fun send(senderId: Long, chatRoomId: Long, request: MessageSendRequest) {
        val sender = getMember(senderId)
        val chatRoom = getChatRoom(chatRoomId)

        check(chatRoom.hasMember(senderId)) { "접근할 수 없는 채팅방입니다." }

        val targetId = getTargetId(chatRoom, senderId)

        check(!blockRepository.existsBlock(senderId, targetId)) { "차단된 회원입니다." }

        // 메시지 저장
        val message = Message(
            chatRoomId = chatRoomId,
            senderId = senderId,
            content = request.content,
            type = TEXT,
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
                TEXT,
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

        // 알림
        sendFcmOfMessage(targetId, chatRoomId, request.content, TEXT)
    }

    @Transactional
    fun sendMedia(senderId: Long, chatRoomId: Long, request: MessageSendMediaRequest) {
        val sender = getMember(senderId)
        val chatRoom = getChatRoom(chatRoomId)

        check(chatRoom.hasMember(senderId)) { "접근할 수 없는 채팅방입니다." }

        val targetId = getTargetId(chatRoom, senderId)

        check(!blockRepository.existsBlock(senderId, targetId)) { "차단된 회원입니다." }

        val isActive = chatRoomSessionManager.isInChatRoom(targetId, chatRoomId)

        val mediaKeys = (request.imageKeys.map { it to IMAGE }) + (request.videoKeys.map { it to VIDEO })
        for ((key, type) in mediaKeys) {
            sendMediaMessage(chatRoom, sender, senderId, targetId, chatRoomId, key, type, isActive)
            sendFcmOfMessage(targetId, chatRoomId, null, type)
        }
    }

    @Transactional(readOnly = true)
    fun gets(query: GetsMessageQuery): CursorResponse<MessageGetResponse> {
        val chatRoom = getChatRoom(query.chatRoomId)

        check(chatRoom.hasMember(query.memberId)) { "접근할 수 없는 채팅방입니다." }

        val result = messageRepository.findAllMessagesByCursor(
            query.chatRoomId,
            query.cursorId,
            query.cursorDate,
            query.size + 1
        ).map { it.toMessageGetResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.messageId,
            nextDateAt = items.lastOrNull()?.createdAt,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun getTarget(memberId: Long, chatRoomId: Long): MessageGetMemberResponse {
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
        val label = if (type == IMAGE) "이미지" else "동영상"

        val message = messageRepository.save(
            Message(chatRoomId = chatRoomId, senderId = senderId, content = key, type = type)
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

    private fun sendFcmOfMessage(memberId: Long, chatRoomId: Long, content: String?, type: MessageType) {
        val preview = content?.let {
            if (it.length > PREVIEW_MAX_LENGTH) {
                content.substring(0, PREVIEW_MAX_LENGTH - 3) + "..."
            } else {
                content
            }
        }

        val body = when (type) {
            IMAGE -> "이미지"
            VIDEO -> "동영상"
            TEXT -> preview
        }

        fcmTokenRepository.findByMemberIdAndIsActiveTrue(memberId).forEach {
            val notification = Notification.builder()
                .setTitle("새로운 쪽지")
                .setBody(body)
                .build()

            val apnsConfig = ApnsConfig.builder()
                .setAps(
                    Aps.builder()
                        .setSound("default")
                        .setCategory("CHAT_MESSAGE")
                        .build()
                )
                .build()

            val message = com.google.firebase.messaging.Message.builder()
                .setToken(it.token)
                .setNotification(notification)
                .setApnsConfig(apnsConfig)
                .putData("type", "CHAT")
                .putData("chatRoomId", chatRoomId.toString())
                .build()

            try {
                FirebaseMessaging.getInstance().send(message)
            } catch (e: FirebaseMessagingException) {
                log.error { "푸시 발송 실패: ${e.message}" }
            }
        }
    }
}