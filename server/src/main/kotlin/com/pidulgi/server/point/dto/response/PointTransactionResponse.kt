package com.pidulgi.server.point.dto.response

import com.pidulgi.server.point.entity.type.TransactionType
import java.time.LocalDateTime

data class PointTransactionResponse(

    val pointTransactionId: Long,
    val type: TransactionType,
    val amount: Long,
    val description: String?,
    val createdAt: LocalDateTime,
)
