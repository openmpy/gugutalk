package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.repository.result.MessageItemResult
import java.time.LocalDateTime

fun interface MessageCustomRepository {

    fun findAllMessagesByCursor(
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MessageItemResult>
}