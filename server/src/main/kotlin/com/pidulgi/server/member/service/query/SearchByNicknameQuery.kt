package com.pidulgi.server.member.service.query

data class SearchByNicknameQuery(

    val memberId: Long,
    val nickname: String,
    val cursorId: Long? = null,
    val cursorSimilarity: Double? = null,
    val size: Int = 20,
) {

    init {
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
