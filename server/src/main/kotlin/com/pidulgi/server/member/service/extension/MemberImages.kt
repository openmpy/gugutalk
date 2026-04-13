package com.pidulgi.server.member.service.extension

import com.pidulgi.server.member.dto.response.MemberImageResponse
import com.pidulgi.server.member.entity.MemberImage

fun List<MemberImage>.toResponses(endpoint: String) = map {

    MemberImageResponse(
        imageId = it.id,
        index = it.sortOrder,
        url = "$endpoint${it.key}",
    )
}
