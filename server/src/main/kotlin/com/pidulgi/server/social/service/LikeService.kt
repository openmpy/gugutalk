package com.pidulgi.server.social.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.social.dto.response.LikeCountResponse
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.LikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(

    private val likeRepository: LikeRepository,
) {

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
        if (likedId != likerId) {
            throw CustomException("자기 자신에게 좋아요를 누를 수 없습니다.")
        }
        val like = (likeRepository.findByLikerIdAndLikedId(likerId, likedId)
            ?: throw CustomException("좋아요를 누른 적이 없습니다."))

        likeRepository.delete(like)
        return LikeCountResponse(likeRepository.countByLikedId(likedId))
    }
}