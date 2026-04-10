package com.pidulgi.server.discovery.service.query

import java.time.LocalDateTime

data class GetRecentMembersQuery(

    val memberId: Long,
    val gender: String = "ALL",
    val cursorId: Long? = null,
    val cursorDate: LocalDateTime? = null,
    val size: Int = 20,
) {

    init {
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
