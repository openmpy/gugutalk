package com.pidulgi.server.chat.repository.result

import java.time.LocalDateTime

data class ChatRoomItemResult(

    val chatRoomId: Long,
    val targetId: Long,
    val nickname: String,
    val profileKey: String?,
    val lastMessage: String?,
    val sortAt: LocalDateTime,
    val unreadCount: Int,
)
