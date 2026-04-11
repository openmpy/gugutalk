package com.pidulgi.server.chat.service.query

data class SearchChatRoomQuery(

    val memberId: Long,
    val nickname: String,
    val cursorId: Long? = null,
    val cursorSimilarity: Double? = null,
    val size: Int = 20,
) {

    init {
        require(nickname.length >= 2) { "검색어는 2자 이상이어야 합니다." }
        require(size in 1..100) { "size는 1 ~ 100 사이여야 합니다." }
    }
}
