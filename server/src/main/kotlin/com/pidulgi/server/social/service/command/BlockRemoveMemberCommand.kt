package com.pidulgi.server.social.service.command

data class BlockRemoveMemberCommand(

    val blockerId: Long,
    val blockedId: Long,
)
