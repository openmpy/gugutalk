package com.pidulgi.server.member.service.command

data class PrivateImageGrantOpenCommand(

    val granterId: Long,
    val granteeId: Long,
) {

    init {
        require(granterId != granteeId) { "자기 자신에게 공개할 수 없습니다." }
    }
}
