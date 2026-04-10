package com.pidulgi.server.social.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomDeleteEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.social.service.event.BlockAddEvent
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class BlockEventListener(

    private val chatRoomRepository: ChatRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun deleteChatRoom(event: BlockAddEvent) {
        val (member1Id, member2Id) = if (event.blockerId < event.blockedId)
            event.blockerId to event.blockedId else event.blockedId to event.blockerId
        val chatRoom = chatRoomRepository.findByMember1IdAndMember2Id(member1Id, member2Id)

        chatRoom?.let {
            it.delete()

            val chatEvent = ChatEvent(
                DELETE_CHAT_ROOM,
                null
            )
            messagingTemplate.convertAndSend(
                "/topic/chat-rooms/${chatRoom.id}",
                chatEvent
            )

            val chatRoomEvent = ChatEvent(
                DELETE_CHAT_ROOM,
                ChatRoomDeleteEvent(
                    chatRoom.id,
                )
            )
            messagingTemplate.convertAndSendToUser(
                event.blockedId.toString(),
                "/queue/chat-rooms",
                chatRoomEvent
            )
        }
    }
}