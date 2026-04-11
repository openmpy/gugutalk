package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.repository.dto.ChatRoomItemResult
import com.pidulgi.server.chat.repository.dto.ChatRoomSearchItemResult
import java.time.LocalDateTime

interface ChatRoomCustomRepository {

    fun findAllChatRoomsByCursor(
        memberId: Long,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<ChatRoomItemResult>

    fun findAllChatRoomsByNicknameWithCursor(
        memberId: Long,
        nickname: String,
        cursorId: Long?,
        cursorSimilarity: Double?,
        size: Int
    ): List<ChatRoomSearchItemResult>
}