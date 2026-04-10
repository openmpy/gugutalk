package com.pidulgi.server.social.service.event

data class BlockAddEvent(

    val blockerId: Long,
    val blockedId: Long,
)
