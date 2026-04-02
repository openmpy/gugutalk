package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.repository.dto.ChatRoomItemResponse
import java.time.LocalDateTime

interface ChatRoomCustomRepository {

    fun findChatRoomsByCursor(
        memberId: Long,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResponse>

    fun searchChatRoomsByCursor(
        memberId: Long,
        keyword: String?,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResponse>
}