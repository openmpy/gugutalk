package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage

data class AdminGetMemberResponse(

    val member: Member,
    val images: List<MemberImage>
)
