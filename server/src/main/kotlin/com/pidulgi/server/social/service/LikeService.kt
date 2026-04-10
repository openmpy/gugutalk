package com.pidulgi.server.social.service

import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.social.dto.response.LikeCountResponse
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.LikeRepository
import com.pidulgi.server.social.service.command.LikeMemberCommand
import com.pidulgi.server.social.service.command.UnlikeMemberCommand
import com.pidulgi.server.social.service.extension.toSettingResponse
import com.pidulgi.server.social.service.query.GetLikedMembersQuery
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val likeRepository: LikeRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun like(command: LikeMemberCommand): LikeCountResponse {
        if (!memberRepository.existsById(command.likerId)) {
            throw CustomException("존재하지 않는 회원입니다.")
        }
        if (likeRepository.existsByLikerIdAndLikedId(command.likerId, command.likedId)) {
            throw CustomException("이미 좋아요를 눌렀습니다.")
        }

        val like = Like(likerId = command.likerId, likedId = command.likerId)
        likeRepository.save(like)

        return LikeCountResponse(likeRepository.countByLikedId(command.likedId))
    }

    @Transactional
    fun unlike(command: UnlikeMemberCommand): LikeCountResponse {
        val like = (likeRepository.findByLikerIdAndLikedId(command.likerId, command.likedId)
            ?: throw CustomException("좋아요를 누른 적이 없습니다."))

        likeRepository.delete(like)
        return LikeCountResponse(likeRepository.countByLikedId(command.likedId))
    }

    @Transactional(readOnly = true)
    fun getLikedMembers(query: GetLikedMembersQuery): CursorResponse<SettingResponse> {
        val result = likeRepository.findLikesByCursor(query.likerId, query.cursorId, query.size + 1)
            .map { it.toSettingResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.id,
            nextDateAt = null,
            hasNext = hasNext
        )
    }
}