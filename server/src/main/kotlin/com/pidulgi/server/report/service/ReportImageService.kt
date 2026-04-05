package com.pidulgi.server.report.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.PresignedUrlsResponse
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.report.dto.ReportGetPresignedUrlsRequest
import com.pidulgi.server.report.entity.ReportImage
import com.pidulgi.server.report.repository.ReportImageRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReportImageService(

    private val memberRepository: MemberRepository,
    private val reportImageRepository: ReportImageRepository,
    private val s3Service: S3Service,
) {

    @Transactional
    fun getPresignedUrls(
        memberId: Long,
        request: ReportGetPresignedUrlsRequest,
    ): PresignedUrlsResponse {
        val member = getMember(memberId)

        val urls = request.images.map {
            val extension = it.contentType.substringAfterLast("/").lowercase()
            val key = "reports/${member.id}/${UUID.randomUUID()}.$extension"

            reportImageRepository.save(ReportImage(key = key))
            s3Service.createPresignedUrl(key, it.contentType)
        }
        return PresignedUrlsResponse(presigned = urls)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}