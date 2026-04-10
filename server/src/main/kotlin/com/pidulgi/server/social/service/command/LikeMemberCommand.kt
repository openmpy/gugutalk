package com.pidulgi.server.social.service.command

data class LikeMemberCommand(

    val likerId: Long,
    val likedId: Long,
) {

    init {
        require(likerId != likedId) { "자기 자신에게 좋아요를 누를 수 없습니다." }
    }
}
