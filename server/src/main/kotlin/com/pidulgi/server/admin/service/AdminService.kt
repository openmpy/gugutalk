package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.admin.service.extension.toAdminGetMemberResponse
import com.pidulgi.server.admin.service.extension.toAdminGetReportResponse
import com.pidulgi.server.admin.service.query.AdminGetMembersQuery
import com.pidulgi.server.admin.service.query.AdminGetReportsQuery
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.dto.response.MemberImageResponse
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.service.extension.toResponses
import com.pidulgi.server.point.repository.PointTransactionRepository
import com.pidulgi.server.point.service.extension.toResponse
import com.pidulgi.server.report.repository.ReportRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val pointTransactionRepository: PointTransactionRepository,
    private val reportRepository: ReportRepository,
    private val s3Service: S3Service,
) {

    @Transactional(readOnly = true)
    fun getMembers(query: AdminGetMembersQuery): CursorResponse<AdminGetMemberResponse> {
        val result = memberRepository.findAllMembersForAdminByCursor(
            query.type,
            query.keyword,
            query.gender,
            query.cursorId,
            query.cursorDate,
            query.size + 1
        ).map { it.toAdminGetMemberResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.memberId,
            nextDateAt = items.lastOrNull()?.updatedAt,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun getReports(query: AdminGetReportsQuery): CursorResponse<AdminGetReportResponse> {
        val result = reportRepository.findAllReportsForAdminByCursor(
            query.type,
            query.keyword,
            query.status,
            query.cursorId,
            query.cursorDate,
            query.size + 1
        ).map { it.toAdminGetReportResponse() }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.reportId,
            nextDateAt = items.lastOrNull()?.createdAt,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun getMember(memberId: Long): AdminGetMemberDetailResponse {
        val member = (memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        val (publicImages, privateImages) = memberImageRepository
            .findAllByMemberId(member.id)
            .sortedBy { it.sortOrder }
            .partition { it.type == ImageType.PUBLIC }

        val publicImageResponse = publicImages.toResponses(endpoint)
        val privateImagesResponse = privateImages.map {
            MemberImageResponse(it.id, it.sortOrder, s3Service.getPresignedUrl(it.key))
        }

        val pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"))
        val pointTransactionsResponse = pointTransactionRepository.findAllByMemberId(member.id, pageRequest)
            .map { it.toResponse() }

        return AdminGetMemberDetailResponse(
            memberId = member.id,
            uuid = member.uuid.value,
            phoneNumber = member.phoneNumber.value,
            nickname = member.nickname.value,
            gender = member.gender,
            birthYear = member.birthYear.value,
            comment = member.comment.value,
            bio = member.bio?.value,
            updatedAt = member.updatedAt,
            createdAt = member.createdAt,
            publicImages = publicImageResponse,
            privateImages = privateImagesResponse,
            pointTransactions = pointTransactionsResponse
        )
    }
}