package com.pidulgi.server.common.dto

data class CursorDistanceResponse<T>(

    val payload: List<T>,
    val nextId: Long?,
    val nextDistance: Double?,
    val hasNext: Boolean
)
