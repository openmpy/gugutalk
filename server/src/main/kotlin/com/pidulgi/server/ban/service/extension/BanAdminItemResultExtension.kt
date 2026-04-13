package com.pidulgi.server.ban.service.extension

import com.pidulgi.server.ban.dto.response.BanGetResponse
import com.pidulgi.server.ban.repository.result.BanAdminItemResult

fun BanAdminItemResult.toGetResponse() = BanGetResponse(

    banId = this.banId,
    uuid = this.uuid,
    reason = this.reason,
    createdAt = this.createdAt,
    expiredAt = this.expiredAt,
)