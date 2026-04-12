package com.pidulgi.server.chat.service.query

import java.time.LocalDateTime

data class GetsMessageQuery(

    val memberId: Long,
    val chatRoomId: Long,
    val cursorId: Long? = null,
    val cursorDate: LocalDateTime? = null,
    val size: Int = 20,
) {

    init {
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
