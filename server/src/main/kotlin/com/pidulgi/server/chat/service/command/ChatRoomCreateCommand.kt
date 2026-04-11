package com.pidulgi.server.chat.service.command

data class ChatRoomCreateCommand(

    val senderId: Long,
    val targetId: Long,
) {

    init {
        require(senderId != targetId) { "자기 자신에게 쪽지를 보낼 수 없습니다." }
    }
}
