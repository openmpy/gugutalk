package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage

data class AdminGetMemberResponse(

    val member: AdminMemberResponse,
    val images: List<MemberImage>,
) {

    companion object {

        fun of(member: Member, images: List<MemberImage>): AdminGetMemberResponse =
            AdminGetMemberResponse(AdminMemberResponse.from(member), images)
    }
}
