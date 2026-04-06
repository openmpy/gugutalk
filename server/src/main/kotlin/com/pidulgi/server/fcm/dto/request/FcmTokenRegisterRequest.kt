package com.pidulgi.server.fcm.dto.request

data class FcmTokenRegisterRequest(

    val token: String,
    val uuid: String?,
)
