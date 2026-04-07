package com.pidulgi.server.admin.dto.response

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.type.Gender
import java.time.LocalDateTime

data class AdminMemberResponse(

    val id: Long,
    val uuid: String,
    val phoneNumber: String,
    val password: String,
    val profileKey: String?,
    val nickname: String,
    val birthYear: Int,
    val gender: Gender,
    val bio: String?,
    val comment: String?,
    val isChatEnabled: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
) {

    companion object {

        fun from(member: Member): AdminMemberResponse = AdminMemberResponse(
            id = member.id,
            uuid = member.uuid,
            phoneNumber = member.phoneNumber,
            password = member.password,
            profileKey = member.profileKey,
            nickname = member.nickname,
            birthYear = member.birthYear,
            gender = member.gender,
            bio = member.bio,
            comment = member.comment,
            isChatEnabled = member.isChatEnabled,
            createdAt = member.createdAt,
            updatedAt = member.updatedAt,
            deletedAt = member.deletedAt,
        )
    }
}
