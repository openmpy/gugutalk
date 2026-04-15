package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.service.batch.ChatRoomBatchProcessor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChatRoomScheduler(

    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomBatchProcessor: ChatRoomBatchProcessor,
) {

    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpChatRooms() {
        val expiredBefore = LocalDateTime.now().minusDays(30)
        val deletedChatRooms = chatRoomRepository.findAllByDeleted(expiredBefore)

        if (deletedChatRooms.isEmpty()) {
            return
        }

        deletedChatRooms.chunked(100) { batch ->
            try {
                chatRoomBatchProcessor.processBatch(batch)
            } catch (e: Exception) {
                log.error(e) { "채팅방 삭제 배치 작업 실패 - ${batch.map { it.id }}" }
            }
        }
    }
}