package com.pidulgi.server.point.service.extension

import com.pidulgi.server.point.dto.response.PointTransactionResponse
import com.pidulgi.server.point.entity.PointTransaction

fun PointTransaction.toResponse() = PointTransactionResponse(

    pointTransactionId = this.id,
    type = this.type,
    amount = this.amount,
    description = this.description,
    createdAt = this.createdAt,
)