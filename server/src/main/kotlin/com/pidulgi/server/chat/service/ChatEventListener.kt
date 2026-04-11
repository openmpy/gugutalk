package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomDeleteEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.service.event.ChatDeleteEvent
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ChatEventListener(

    private val chatRoomRepository: ChatRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun deleteChatRoom(event: ChatDeleteEvent) {
        val chatEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            null
        )
        messagingTemplate.convertAndSend(
            "/topic/chat-rooms/${event.chatRoomId}",
            chatEvent
        )

        // 채널 구독 개인 전송
        val chatRoomEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            ChatRoomDeleteEvent(
                event.chatRoomId,
            )
        )
        messagingTemplate.convertAndSendToUser(
            event.targetId.toString(),
            "/queue/chat-rooms",
            chatRoomEvent
        )
    }
}