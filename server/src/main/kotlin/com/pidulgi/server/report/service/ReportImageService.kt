package com.pidulgi.server.report.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.PresignedUrlsResponse
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.report.dto.ReportGetPresignedUrlsRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReportImageService(

    private val memberRepository: MemberRepository,
    private val s3Service: S3Service,
) {

    @Transactional(readOnly = true)
    fun getPresignedUrls(
        memberId: Long,
        request: ReportGetPresignedUrlsRequest,
    ): PresignedUrlsResponse {
        val member = getMember(memberId)

        val urls = request.images.map {
            val extension = it.contentType.substringAfterLast("/").lowercase()
            val key = "reports/${member.id}/${UUID.randomUUID()}.$extension"

            s3Service.createPresignedUrl(key, it.contentType)
        }
        return PresignedUrlsResponse(presigned = urls)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}