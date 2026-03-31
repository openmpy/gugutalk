package com.pidulgi.server.social.service

import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.social.dto.response.LikeCountResponse
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.LikeRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class LikeService(

    private val likeRepository: LikeRepository,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional
    fun like(likerId: Long, likedId: Long): LikeCountResponse {
        if (likedId != likerId) {
            throw CustomException("자기 자신에게 좋아요를 누를 수 없습니다.")
        }
        if (likeRepository.existsByLikerIdAndLikedId(likerId, likedId)) {
            throw CustomException("이미 좋아요를 눌렀습니다.")
        }

        val like = Like(
            likerId = likerId,
            likedId = likedId,
        )
        likeRepository.save(like)

        return LikeCountResponse(likeRepository.countByLikedId(likedId))
    }

    @Transactional
    fun unlike(likerId: Long, likedId: Long): LikeCountResponse {
        val like = (likeRepository.findByLikerIdAndLikedId(likerId, likedId)
            ?: throw CustomException("좋아요를 누른 적이 없습니다."))

        likeRepository.delete(like)
        return LikeCountResponse(likeRepository.countByLikedId(likedId))
    }

    @Transactional(readOnly = true)
    fun getLikedMembers(
        likerId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int = 20
    ): CursorResponse<SettingResponse> {
        val result = likeRepository.findLikesByCursor(likerId, cursorId, cursorDate, size + 1)
            .map {
                SettingResponse(
                    id = it.likeId,
                    memberId = it.memberId,
                    nickname = it.nickname,
                    gender = it.gender,
                    age = LocalDate.now().year - it.birthYear,
                    profileUrl = it.profileKey?.let { "$endpoint$it" },
                    createdAt = it.createdAt,
                )
            }
        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = if (hasNext) items.last().id else null,
            nextDateAt = if (hasNext) items.last().createdAt else null,
            hasNext = hasNext
        )
    }
}