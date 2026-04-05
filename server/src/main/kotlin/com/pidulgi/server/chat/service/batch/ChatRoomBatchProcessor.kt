package com.pidulgi.server.chat.service.batch

import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.common.s3.S3CleanupEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ChatRoomBatchProcessor(

    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun processBatch(chatRooms: List<ChatRoom>) {
        val chatRoomIds = chatRooms.map { it.id }
        val s3Keys = messageRepository.findS3KeysByChatRoomIdIn(chatRoomIds)

        messageRepository.hardDeleteAllByChatRoomIdIn(chatRoomIds)
        chatRoomRepository.hardDeleteByIdIn(chatRoomIds)

        applicationEventPublisher.publishEvent(S3CleanupEvent(s3Keys))
    }
}