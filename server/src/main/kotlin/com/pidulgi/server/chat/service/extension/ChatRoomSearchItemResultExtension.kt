package com.pidulgi.server.chat.service.extension

import com.pidulgi.server.chat.dto.response.ChatRoomSearchResponse
import com.pidulgi.server.chat.repository.dto.ChatRoomSearchItemResult

fun ChatRoomSearchItemResult.toChatRoomSearchResponse(endpoint: String) = ChatRoomSearchResponse(

    chatRoomId = this.chatRoomId,
    targetId = this.targetId,
    nickname = this.nickname,
    profileUrl = this.profileKey?.let { "$endpoint$it" },
    lastMessage = this.lastMessage,
    sortAt = this.sortAt,
    unreadCount = this.unreadCount,
    similarityScore = this.similarityScore,
)