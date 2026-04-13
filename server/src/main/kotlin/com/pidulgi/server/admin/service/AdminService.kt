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
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.service.extension.toResponses
import com.pidulgi.server.point.repository.PointRepository
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
    private val pointRepository: PointRepository,
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
        val member = findMember(memberId)

        // 이미지
        val (publicImages, privateImages) = memberImageRepository
            .findAllByMemberId(member.id)
            .sortedBy { it.sortOrder }
            .partition { it.type == ImageType.PUBLIC }

        val publicImageResponse = publicImages.toResponses(endpoint)
        val privateImagesResponse = privateImages.map {
            MemberImageResponse(it.id, it.sortOrder, s3Service.getPresignedUrl(it.key))
        }

        // 포인트
        val point = (pointRepository.findByMemberId(member.id)
            ?: throw CustomException("포인트 정보를 찾을 수 없습니다."))

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
            point = point.balance,
            pointTransactions = pointTransactionsResponse
        )
    }

    @Transactional
    fun sanitizeNickname(memberId: Long) {
        val member = findMember(memberId)
        member.sanitizeNickname()
    }

    @Transactional
    fun sanitizeComment(memberId: Long) {
        val member = findMember(memberId)
        member.sanitizeComment()
    }

    @Transactional
    fun sanitizeBio(memberId: Long) {
        val member = findMember(memberId)
        member.sanitizeBio()
    }

    @Transactional
    fun deleteMemberImage(memberId: Long, imageId: Long) {
        val member = findMember(memberId)
        val memberImage = (memberImageRepository.findByIdOrNull(imageId)
            ?: throw CustomException("존재하지 않는 회원 이미지입니다."))

        if (member.id != memberImage.memberId) {
            throw CustomException("해당 회원의 이미지가 아닙니다.")
        }

        memberImageRepository.delete(memberImage)
        s3Service.delete(memberImage.key)

        val remainingImages = memberImageRepository.findAllByMemberIdAndTypeOrderBySortOrder(memberId, memberImage.type)
        remainingImages.forEachIndexed { index, image ->
            image.updateSortOrder(index)
        }

        if (memberImage.type == ImageType.PUBLIC && memberImage.sortOrder == 0) {
            member.updateProfileKey(remainingImages.firstOrNull()?.key)
        }
    }

    private fun findMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}