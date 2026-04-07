package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.type.ImageType
import java.time.LocalDateTime

data class AdminGetMemberDetailResponse(

    val memberId: Long,
    val uuid: String,
    val phoneNumber: String,
    val nickname: String,
    val birthYear: Int,
    val gender: Gender,
    val bio: String?,
    val comment: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
    val images: List<AdminGetMemberImageResponse>,
) {

    data class AdminGetMemberImageResponse(

        val imageId: Long,
        val url: String,
        val key: String,
        val type: ImageType,
        val sortOrder: Int,
        val createdAt: LocalDateTime,
    )
}
