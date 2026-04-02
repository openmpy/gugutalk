package com.pidulgi.server.chat.websocket

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatRoomSessionManager {

    private val chatRoom = ConcurrentHashMap<Long, Long>()

    fun enter(memberId: Long, chatRoomId: Long) {
        chatRoom[memberId] = chatRoomId
    }

    fun leave(memberId: Long) {
        chatRoom.remove(memberId)
    }

    fun isInChatRoom(memberId: Long, chatRoomId: Long): Boolean {
        return chatRoom[memberId] == chatRoomId
    }
}