package com.pidulgi.server.discovery.service.query

data class GetLocationMembersQuery(

    val memberId: Long,
    val gender: String = "ALL",
    val cursorId: Long? = null,
    val cursorDistance: Double? = null,
    val size: Int = 20,
) {

    init {
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
