package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.service.batch.ChatRoomBatchProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChatRoomScheduler(

    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomBatchProcessor: ChatRoomBatchProcessor,
) {

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpChatRooms() {
        val expiredBefore = LocalDateTime.now().minusDays(30)
        val deletedChatRooms = chatRoomRepository.findAllDeleted(expiredBefore)

        if (deletedChatRooms.isEmpty()) {
            return
        }

        deletedChatRooms.chunked(100) { batch ->
            chatRoomBatchProcessor.processBatch(batch)
        }
    }
}