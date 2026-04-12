package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.service.event.ChatQueueEvent
import com.pidulgi.server.chat.service.event.ChatTopicEvent
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ChatEventListener(

    private val messagingTemplate: SimpMessagingTemplate,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun topic(event: ChatTopicEvent) {
        messagingTemplate.convertAndSend(
            "/topic/chat-rooms/${event.chatRoomId}",
            event.chatEvent
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun queue(event: ChatQueueEvent) {
        messagingTemplate.convertAndSendToUser(
            event.memberId.toString(),
            "/queue/chat-rooms",
            event.chatEvent
        )
    }
}