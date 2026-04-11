package com.pidulgi.server.common.dto

data class CursorSimilarityResponse<T>(

    val payload: List<T>,
    val nextId: Long?,
    val nextSimilarity: Double?,
    val hasNext: Boolean
)
