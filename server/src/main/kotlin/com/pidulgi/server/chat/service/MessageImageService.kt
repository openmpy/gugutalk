package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.request.MediaGetPresignedUrlsRequest
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.common.s3.dto.response.PresignedUrlsResponse
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MessageImageService(

    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val s3Service: S3Service,
) {

    @Transactional(readOnly = true)
    fun getPresignedUrls(
        memberId: Long,
        chatRoomId: Long,
        request: MediaGetPresignedUrlsRequest,
    ): PresignedUrlsResponse {
        val member = getMember(memberId)
        val chatRoom = (chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다."))

        if (chatRoom.member1Id != memberId && chatRoom.member2Id != memberId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        val urls = request.medias.map {
            val extension = it.contentType.substringAfterLast("/").lowercase()
            val key = "chats/${chatRoomId}/${member.id}/${UUID.randomUUID()}.$extension"

            s3Service.createPresignedUrl(key, it.contentType)
        }
        return PresignedUrlsResponse(presigned = urls)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}