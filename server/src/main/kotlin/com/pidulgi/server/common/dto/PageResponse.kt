package com.pidulgi.server.common.dto

data class PageResponse<T>(

    val payload: List<T>,
    val page: Int,
    val hasNext: Boolean
)