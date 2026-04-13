package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.dto.response.MemberImageResponse
import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.point.dto.response.PointTransactionResponse
import java.time.LocalDateTime

data class AdminGetMemberDetailResponse(

    val memberId: Long,
    val uuid: String,
    val phoneNumber: String,
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val comment: String?,
    val bio: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    // 이미지
    val publicImages: List<MemberImageResponse>,
    val privateImages: List<MemberImageResponse>,

    // 포인트 내역
    val pointTransactions: List<PointTransactionResponse>,
)
