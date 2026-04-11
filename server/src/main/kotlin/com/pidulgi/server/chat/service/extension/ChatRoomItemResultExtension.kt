package com.pidulgi.server.chat.service.extension

import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.repository.result.ChatRoomItemResult

fun ChatRoomItemResult.toChatRoomGetResponse(endpoint: String) = ChatRoomGetResponse(

    chatRoomId = this.chatRoomId,
    targetId = this.targetId,
    nickname = this.nickname.value,
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    lastMessage = this.lastMessage,
    sortAt = this.sortAt,
    unreadCount = this.unreadCount,
)