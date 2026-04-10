package com.pidulgi.server.social.service.command

data class BlockAddMemberCommand(

    val blockerId: Long,
    val blockedId: Long,
) {

    init {
        require(blockerId != blockedId) { "자기 자신을 차단할 수 없습니다." }
    }
}
