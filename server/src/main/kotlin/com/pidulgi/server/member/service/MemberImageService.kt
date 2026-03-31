package com.pidulgi.server.member.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.PresignedUrlsResponse
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.dto.request.MemberGetPresignedUrlsRequest
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MemberImageService(

    private val memberRepository: MemberRepository,
    private val s3Service: S3Service,
) {

    @Transactional(readOnly = true)
    fun getPresignedUrls(
        memberId: Long,
        request: MemberGetPresignedUrlsRequest
    ): PresignedUrlsResponse {
        val member = getMember(memberId)

        val urls = request.images.map {
            val extension = it.contentType.substringAfterLast("/").lowercase()
            val imageTypeLowerCase = it.imageType.name.lowercase()
            val key = "members/${member.id}/$imageTypeLowerCase/${UUID.randomUUID()}.$extension"

            s3Service.createPresignedUrl(key, it.contentType)
        }
        return PresignedUrlsResponse(presigned = urls)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}