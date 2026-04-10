package com.pidulgi.server.social.service.query

data class GetBlockedMembersQuery(

    val blockerId: Long,
    val cursorId: Long? = null,
    val size: Int = 20,
) {

    init {
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
