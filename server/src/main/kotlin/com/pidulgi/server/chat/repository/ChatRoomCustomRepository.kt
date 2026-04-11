package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.repository.dto.ChatRoomItemResult
import java.time.LocalDateTime

interface ChatRoomCustomRepository {

    fun findAllChatRoomsByCursor(
        memberId: Long,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResult>

    fun searchChatRoomsByCursor(
        memberId: Long,
        keyword: String?,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResult>
}