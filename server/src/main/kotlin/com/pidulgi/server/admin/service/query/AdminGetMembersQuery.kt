package com.pidulgi.server.admin.service.query

import java.time.LocalDateTime

data class AdminGetMembersQuery(

    val type: String = "NICKNAME",
    val keyword: String = "",
    val gender: String = "ALL",
    val cursorId: Long? = null,
    val cursorDate: LocalDateTime? = null,
    val size: Int = 20,
) {

    init {
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
