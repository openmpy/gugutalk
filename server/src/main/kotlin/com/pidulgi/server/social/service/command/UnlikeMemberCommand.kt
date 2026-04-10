package com.pidulgi.server.social.service.command

data class UnlikeMemberCommand(

    val likerId: Long,
    val likedId: Long,
)
