package com.pidulgi.server.member.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.member.service.event.MemberWithdrawEvent
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MemberEventListener(

    private val chatRoomRepository: ChatRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun deleteChatRooms(event: MemberWithdrawEvent) {
        chatRoomRepository.findAllByMember1IdOrMember2Id(event.memberId, event.memberId).forEach {
            it.delete()

            val chatEvent = ChatEvent(
                DELETE_CHAT_ROOM,
                null
            )
            messagingTemplate.convertAndSend(
                "/topic/chat-rooms/${it.id}",
                chatEvent
            )
        }
    }
}