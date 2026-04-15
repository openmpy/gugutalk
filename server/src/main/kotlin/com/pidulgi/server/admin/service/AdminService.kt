package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.request.AdminLoginRequest
import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.admin.service.extension.toAdminGetMemberResponse
import com.pidulgi.server.admin.service.extension.toAdminGetReportResponse
import com.pidulgi.server.admin.service.query.AdminGetMembersQuery
import com.pidulgi.server.admin.service.query.AdminGetReportsQuery
import com.pidulgi.server.auth.dto.response.LoginResponse
import com.pidulgi.server.auth.service.AUTH_REFRESH_TOKEN_KEY
import com.pidulgi.server.common.auth.JwtProvider
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.dto.response.MemberImageResponse
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.entity.type.MemberRole
import com.pidulgi.server.member.entity.vo.MemberPhoneNumber
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.service.extension.toResponses
import com.pidulgi.server.point.repository.PointRepository
import com.pidulgi.server.point.repository.PointTransactionRepository
import com.pidulgi.server.point.service.extension.toResponse
import com.pidulgi.server.report.entity.type.ReportStatus
import com.pidulgi.server.report.repository.ReportImageRepository
import com.pidulgi.server.report.repository.ReportRepository
import com.pidulgi.server.report.service.extension.toResponses
import org.apache.hc.client5.http.auth.InvalidCredentialsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
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
    private val reportImageRepository: ReportImageRepository,
    private val s3Service: S3Service,
    private val redisTemplate: StringRedisTemplate,
    private val jwtProvider: JwtProvider,
) {

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

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

    @Transactional(readOnly = true)
    fun getReport(reportId: Long): AdminGetReportDetailResponse {
        val report = (reportRepository.findByIdOrNull(reportId)
            ?: throw CustomException("존재하지 않는 신고 정보입니다."))

        val reportImageResponses = reportImageRepository.findAllByReportId(report.id)
            .map { it.toResponses(s3Service.getPresignedUrl(it.key)) }

        return AdminGetReportDetailResponse(
            reportId = report.id,
            reporterId = report.reporterId,
            reporterUuid = report.reporterUuid,
            reporterPhoneNumber = report.reporterPhoneNumber,
            reporterNickname = report.reporterNickname,
            reportedId = report.reportedId,
            reportedUuid = report.reportedUuid,
            reportedPhoneNumber = report.reportedPhoneNumber,
            reportedNickname = report.reportedNickname,
            type = report.type,
            reason = report.reason,
            status = report.status,
            createdAt = report.createdAt,
            images = reportImageResponses,
        )
    }

    @Transactional
    fun updateReport(reportId: Long, status: String) {
        val report = (reportRepository.findByIdOrNull(reportId)
            ?: throw CustomException("존재하지 않는 신고 정보입니다."))

        val reportStatus = ReportStatus.from(status)
        report.updateStatus(reportStatus)
    }

    @Transactional
    fun login(request: AdminLoginRequest): LoginResponse {
        val memberPhoneNumber = MemberPhoneNumber(request.phoneNumber)

        val member = (memberRepository.findByPhoneNumber(memberPhoneNumber)
            ?: throw CustomException("다시 한번 확인해주시길 바랍니다."))

        if (!passwordEncoder.matches(request.password, member.password.value)) {
            throw InvalidCredentialsException("다시 한번 확인해주시길 바랍니다.")
        }

        if (member.role != MemberRole.ADMIN) {
            throw CustomException("관리자 계정이 아닙니다.")
        }

        val accessToken = jwtProvider.generateAccessToken(member.id, member.role, member.nickname)
        val refreshToken = jwtProvider.generateRefreshToken(member.id)

        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + refreshToken
        redisTemplate.opsForValue().set(refreshTokenKey, member.id.toString())

        return LoginResponse(
            member.id,
            accessToken,
            refreshToken,
        )
    }

    private fun findMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}