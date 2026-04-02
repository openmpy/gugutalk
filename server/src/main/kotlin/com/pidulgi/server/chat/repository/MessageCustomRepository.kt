package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.repository.dto.MessageItemResponse
import java.time.LocalDateTime

interface MessageCustomRepository {

    fun findAllChatRoomByCursor(
        memberId: Long,
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MessageItemResponse>
}