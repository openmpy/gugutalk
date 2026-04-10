package com.pidulgi.server.member.service.command

data class PrivateImageGrantCloseCommand(

    val granterId: Long,
    val granteeId: Long,
)
