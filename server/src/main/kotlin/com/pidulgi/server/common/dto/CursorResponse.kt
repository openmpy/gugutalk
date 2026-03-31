package com.pidulgi.server.common.dto

import java.time.LocalDateTime

data class CursorResponse<T>(

    val payload: List<T>,
    val nextId: Long?,
    val nextDateAt: LocalDateTime?,
    val hasNext: Boolean
)
