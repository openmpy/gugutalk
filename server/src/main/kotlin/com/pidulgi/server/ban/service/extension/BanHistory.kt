package com.pidulgi.server.ban.service.extension

import com.pidulgi.server.ban.dto.response.BanHistoryResponse
import com.pidulgi.server.ban.entity.BanHistory

fun BanHistory.toGetResponse() = BanHistoryResponse(

    type = this.type,
    phoneNumber = this.phoneNumber,
    reason = this.reason,
    createdAt = this.createdAt,
    expiredAt = this.expiredAt,
)