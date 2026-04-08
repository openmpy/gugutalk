package com.pidulgi.server.report.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.report.dto.ReportCreateRequest
import com.pidulgi.server.report.entity.Report
import com.pidulgi.server.report.repository.ReportImageRepository
import com.pidulgi.server.report.repository.ReportRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReportService(

    private val reportRepository: ReportRepository,
    private val reportImageRepository: ReportImageRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun create(
        reporterId: Long,
        reportedId: Long,
        request: ReportCreateRequest
    ) {
        val reporter = getMember(reporterId)
        val reported = getMember(reportedId)

        val report = Report(
            reporterId = reporter.id,
            reporterUuid = reporter.uuid.value,
            reporterPhoneNumber = reporter.phoneNumber,
            reporterNickname = reporter.nickname,

            reportedId = reported.id,
            reportedUuid = reported.uuid.value,
            reportedPhoneNumber = reported.phoneNumber,
            reportedNickname = reported.nickname,

            type = request.type,
            reason = request.reason,
        )
        reportRepository.save(report)

        val keys = request.images.map { it.key }
        val images = reportImageRepository.findAllByKeyIn(keys)

        images.forEach {
            it.upload(report.id)
        }
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}