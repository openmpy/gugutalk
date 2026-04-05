package com.pidulgi.server.member.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.PresignedUrlsResponse
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.dto.request.MemberGetPresignedUrlsRequest
import com.pidulgi.server.member.dto.response.MemberGetPrivateImagesResponse
import com.pidulgi.server.member.dto.response.MemberGetPrivateImagesResponse.MemberPrivateImageResponse
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MemberImageService(

    private val memberRepository: MemberRepository,
    private val privateImageGrantRepository: PrivateImageGrantRepository,
    private val memberImageRepository: MemberImageRepository,
    private val s3Service: S3Service,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional
    fun getPresignedUrls(
        memberId: Long,
        request: MemberGetPresignedUrlsRequest
    ): PresignedUrlsResponse {
        val member = getMember(memberId)

        val urls = request.images.map {
            val extension = it.contentType.substringAfterLast("/").lowercase()
            val imageTypeLowerCase = it.imageType.name.lowercase()
            val key = "members/${member.id}/$imageTypeLowerCase/${UUID.randomUUID()}.$extension"

            val memberImage = MemberImage(
                key = key,
                type = it.imageType
            )
            memberImageRepository.save(memberImage)

            s3Service.createPresignedUrl(key, it.contentType)
        }
        return PresignedUrlsResponse(presigned = urls)
    }

    @Transactional(readOnly = true)
    fun getPrivateImages(granteeId: Long, granterId: Long): MemberGetPrivateImagesResponse {
        if (!privateImageGrantRepository.existsByGranterIdAndGranteeId(granterId, granteeId)) {
            throw CustomException("비밀 사진이 공개되지 않았습니다.")
        }

        val privateImages = memberImageRepository.findByMemberIdAndTypeOrderBySortOrder(
            granterId,
            ImageType.PRIVATE
        ).map {
            MemberPrivateImageResponse(
                url = s3Service.getPresignedUrl(it.key)
            )
        }
        return MemberGetPrivateImagesResponse(privateImages)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}