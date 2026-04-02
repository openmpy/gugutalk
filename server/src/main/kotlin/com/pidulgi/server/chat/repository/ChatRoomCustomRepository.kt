package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.repository.dto.ChatRoomItemResponse
import java.time.LocalDateTime

interface ChatRoomCustomRepository {

    fun findMemberByCursor(
        memberId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResponse>
}