package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse.AdminGetMemberImageResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.common.dto.PageResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class AdminService(
    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val s3Service: S3Service,
) {

    @Transactional(readOnly = true)
    fun getMembers(
        gender: String,
        page: Int,
        size: Int
    ): PageResponse<AdminGetMemberResponse> {
        val offset = page * size
        val result = memberRepository.findAllByPage(gender, offset, size + 1)
            .map {
                AdminGetMemberResponse(
                    memberId = it.id,
                    profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                    nickname = it.nickname,
                    age = LocalDate.now().year - it.birthYear,
                    gender = it.gender,
                    comment = it.comment,
                    updatedAt = it.updatedAt,
                    deletedAt = it.deletedAt
                )
            }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return PageResponse(
            payload = items,
            page = page,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun getMember(memberId: Long): AdminGetMemberDetailResponse {
        val member = (memberRepository.findByIdOrNullNative(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        val publicImages = memberImageRepository.findByMemberIdAndTypeOrderBySortOrder(
            memberId, ImageType.PUBLIC
        ).map {
            AdminGetMemberImageResponse(
                it.id,
                url = "$endpoint${it.key}",
                it.key,
                it.type,
                it.sortOrder,
                it.createdAt
            )
        }
        val privateImages = memberImageRepository.findByMemberIdAndTypeOrderBySortOrder(
            memberId, ImageType.PRIVATE
        ).map {
            AdminGetMemberImageResponse(
                it.id,
                url = s3Service.getPresignedUrl(it.key),
                it.key,
                it.type,
                it.sortOrder,
                it.createdAt
            )
        }

        return AdminGetMemberDetailResponse(
            memberId = member.id,
            uuid = member.uuid,
            phoneNumber = member.phoneNumber,
            nickname = member.nickname,
            birthYear = member.birthYear,
            gender = member.gender,
            bio = member.bio,
            comment = member.comment,
            createdAt = member.createdAt,
            updatedAt = member.updatedAt,
            deletedAt = member.deletedAt,
            images = publicImages + privateImages,
        )
    }

    @Transactional
    fun updateMemberNickname(memberId: Long) {
        val member = (memberRepository.findByIdOrNullNative(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        val nickname = "닉네임_" + UUID.randomUUID().toString().replace("-", "").substring(0, 6)
        member.updateNickname(nickname)
    }

    @Transactional
    fun updateMemberComment(memberId: Long) {
        val member = (memberRepository.findByIdOrNullNative(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        member.updateComment("부적절한 코멘트 내용입니다.")
    }

    @Transactional
    fun updateMemberBio(memberId: Long) {
        val member = (memberRepository.findByIdOrNullNative(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        member.updateBio("부적절한 자기소개 내용입니다.")
    }

    @Transactional(readOnly = true)
    fun searchMembers(
        keyword: String,
        gender: String,
        page: Int,
        size: Int
    ): PageResponse<AdminGetMemberResponse> {
        val offset = page * size
        val result = memberRepository.findAllByNicknamePage(keyword, gender, offset, size + 1)
            .map {
                AdminGetMemberResponse(
                    memberId = it.id,
                    profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                    nickname = it.nickname,
                    age = LocalDate.now().year - it.birthYear,
                    gender = it.gender,
                    comment = it.comment,
                    updatedAt = it.updatedAt,
                    deletedAt = it.deletedAt
                )
            }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return PageResponse(
            payload = items,
            page = page,
            hasNext = hasNext
        )
    }
}