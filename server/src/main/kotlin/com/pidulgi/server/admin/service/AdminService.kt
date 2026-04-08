package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse.AdminGetMemberImageResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportDetailResponse.AdminGetReportImageResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.common.dto.PageResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.report.entity.type.ReportStatus
import com.pidulgi.server.report.repository.ReportImageRepository
import com.pidulgi.server.report.repository.ReportRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class AdminService(
    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val reportRepository: ReportRepository,
    private val reportImageRepository: ReportImageRepository,
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
            uuid = member.uuid.value,
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

    @Transactional
    fun deleteMemberImage(memberId: Long, imageId: Long) {
        val member = (memberRepository.findByIdOrNullNative(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))
        val memberImage = (memberImageRepository.findByIdOrNull(imageId)
            ?: throw CustomException("존재하지 않는 회원 이미지입니다."))

        memberImageRepository.delete(memberImage)
        s3Service.delete(memberImage.key)

        val memberImages = memberImageRepository.findByMemberIdAndTypeOrderBySortOrder(
            memberId, memberImage.type
        )
        memberImages.forEachIndexed { index, image ->
            image.updateSortOrder(index)
        }

        if (memberImage.type == ImageType.PUBLIC) {
            val newProfileKey = memberImages.firstOrNull()?.key
            member.updateProfileKey(newProfileKey)
        }
    }

    @Transactional(readOnly = true)
    fun getReports(
        status: String,
        page: Int,
        size: Int
    ): PageResponse<AdminGetReportResponse> {
        val offset = page * size
        val result = reportRepository.findAllByPage(status, offset, size + 1)
            .map {
                AdminGetReportResponse(
                    reportId = it.id,
                    type = it.type,
                    reporterNickname = it.reporterNickname,
                    reportedNickname = it.reportedNickname,
                    reason = it.reason,
                    createdAt = it.createdAt,
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
    fun getReport(reportId: Long): AdminGetReportDetailResponse {
        val report = (reportRepository.findByIdOrNull(reportId)
            ?: throw CustomException("존재하지 않는 신고입니다."))
        val reportImages = reportImageRepository.findAllByReportId(reportId).map {
            AdminGetReportImageResponse(
                imageId = it.id,
                url = s3Service.getPresignedUrl(it.key),
                key = it.key,
                sortOrder = it.sortOrder,
                createdAt = it.createdAt,
            )
        }

        return AdminGetReportDetailResponse(
            reportId = reportId,
            status = report.status,
            type = report.type,
            reporterId = report.reporterId,
            reporterUuid = report.reporterUuid,
            reporterPhone = report.reporterPhoneNumber,
            reporterNickname = report.reporterNickname,
            reportedId = report.reportedId,
            reportedUuid = report.reportedUuid,
            reportedPhone = report.reportedPhoneNumber,
            reportedNickname = report.reportedNickname,
            reason = report.reason,
            createdAt = report.createdAt,
            images = reportImages
        )
    }

    @Transactional
    fun updateReport(reportId: Long, reportStatus: String) {
        val report = (reportRepository.findByIdOrNull(reportId)
            ?: throw CustomException("존재하지 않는 신고입니다."))

        report.updateStatus(ReportStatus.valueOf(reportStatus))
    }

    @Transactional(readOnly = true)
    fun searchReports(
        type: String,
        keyword: String,
        status: String,
        page: Int,
        size: Int
    ): PageResponse<AdminGetReportResponse> {
        val offset = page * size
        val result = if (type.equals("reporter", ignoreCase = true)) {
            reportRepository.findAllByReporterNicknamePage(keyword, status, offset, size + 1)
        } else {
            reportRepository.findAllByReportedNicknamePage(keyword, status, offset, size + 1)
        }.map {
            AdminGetReportResponse(
                reportId = it.id,
                type = it.type,
                reporterNickname = it.reporterNickname,
                reportedNickname = it.reportedNickname,
                reason = it.reason,
                createdAt = it.createdAt,
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