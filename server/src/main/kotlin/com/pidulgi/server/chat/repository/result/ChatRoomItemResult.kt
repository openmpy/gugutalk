package com.pidulgi.server.chat.repository.result

import com.pidulgi.server.member.entity.vo.MemberNickname
import java.time.LocalDateTime

data class ChatRoomItemResult(

    val chatRoomId: Long,
    val targetId: Long,
    val nickname: MemberNickname,
    val profileKey: String?,
    val lastMessage: String?,
    val sortAt: LocalDateTime,
    val unreadCount: Int,
)
