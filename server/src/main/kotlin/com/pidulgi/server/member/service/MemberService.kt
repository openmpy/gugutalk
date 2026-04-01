package com.pidulgi.server.member.service

import com.pidulgi.server.auth.service.AUTH_ACCESS_TOKEN_BLACKLIST_KEY
import com.pidulgi.server.auth.service.AUTH_REFRESH_TOKEN_KEY
import com.pidulgi.server.common.auth.ACCESS_TOKEN_EXPIRE_HOURS
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.member.dto.request.MemberBumpRequest
import com.pidulgi.server.member.dto.request.MemberUpdateCommentRequest
import com.pidulgi.server.member.dto.request.MemberWithdrawRequest
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.dto.response.MemberGetResponse
import com.pidulgi.server.member.dto.response.MemberImageResponse
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate

@Service
class MemberService(

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val likeRepository: LikeRepository,
    private val blockRepository: BlockRepository,
    private val privateImageGrantRepository: PrivateImageGrantRepository,
    private val redisTemplate: StringRedisTemplate,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional(readOnly = true)
    fun getMe(memberId: Long): MemberGetMeResponse {
        val member = getMember(memberId)
        val images = memberImageRepository.findByMemberIdAndTypeOrderBySortOrder(
            member.id,
            ImageType.PUBLIC
        ).map {
            MemberImageResponse(
                it.id,
                it.sortOrder,
                endpoint + it.key
            )
        }

        return MemberGetMeResponse(
            member.id,
            images,
            member.nickname,
            member.gender,
            LocalDate.now().year - member.birthYear,
            member.bio,
            0
        )
    }

    @Transactional(readOnly = true)
    fun getMember(memberId: Long, targetId: Long): MemberGetResponse {
        val member = (memberRepository.findByIdOrNull(targetId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        val images = memberImageRepository
            .findByMemberIdAndTypeOrderBySortOrder(member.id, ImageType.PUBLIC)
            .map { MemberImageResponse(it.id, it.sortOrder, endpoint + it.key) }

        val isLiked = likeRepository.existsByLikerIdAndLikedId(memberId, targetId)
        val isBlocked = blockRepository.existsByBlockerIdAndBlockedId(memberId, targetId)
        val isPrivateImageGranted = privateImageGrantRepository.existsByGranterIdAndGranteeId(
            memberId, targetId
        )
        val isPrivateImageGrantedByTarget =
            privateImageGrantRepository.existsByGranterIdAndGranteeId(
                targetId, memberId
            )
        val likes = likeRepository.countByLikedId(targetId)
        val distance = memberRepository.getDistanceBetween(memberId, targetId)

        return MemberGetResponse(
            memberId = member.id,
            images = images,
            nickname = member.nickname,
            gender = member.gender,
            age = LocalDate.now().year - member.birthYear,
            bio = member.bio,
            likes = likes,
            distance = distance,
            updatedAt = member.updatedAt,
            isLiked = isLiked,
            isBlocked = isBlocked,
            isPrivateImageGranted = isPrivateImageGranted,
            isPrivateImageGrantedByTarget = isPrivateImageGrantedByTarget,
        )
    }

    @Transactional
    fun withdraw(memberId: Long, request: MemberWithdrawRequest) {
        val member = getMember(memberId)

        val accessTokenBlacklist = AUTH_ACCESS_TOKEN_BLACKLIST_KEY + request.accessToken
        val refreshToken = AUTH_REFRESH_TOKEN_KEY + request.refreshToken

        redisTemplate.opsForValue().set(
            accessTokenBlacklist,
            memberId.toString(),
            Duration.ofHours(ACCESS_TOKEN_EXPIRE_HOURS)
        )
        redisTemplate.delete(refreshToken)

        member.withdraw()
    }

    @Transactional
    fun bump(memberId: Long, request: MemberBumpRequest) {
        val member = getMember(memberId)
        val point = request.longitude?.let { longitude ->
            request.latitude?.let { latitude ->
                GeometryFactory().createPoint(Coordinate(longitude, latitude))
            }
        }
        member.bump(point)
    }

    @Transactional
    fun updateComment(memberId: Long, request: MemberUpdateCommentRequest) {
        val member = getMember(memberId)
        member.updateComment(request.comment)
    }

    @Transactional(readOnly = true)
    fun searchByNickname(
        memberId: Long,
        keyword: String,
        cursorId: Long?,
        size: Int
    ): CursorResponse<MemberDiscoveryResponse> {
        if (keyword.length < 2) {
            throw CustomException("검색어는 2자 이상이어야 합니다.")
        }

        val result = memberRepository.searchByNickname(
            memberId,
            keyword,
            cursorId,
            size + 1
        ).map {
            MemberDiscoveryResponse(
                memberId = it.memberId,
                profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                nickname = it.nickname,
                gender = it.gender,
                age = LocalDate.now().year - it.birthYear,
                comment = it.comment,
                distance = it.distance,
                likes = it.likes,
                updatedAt = it.updatedAt,
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = if (hasNext) items.last().memberId else null,
            nextDateAt = null,
            hasNext = hasNext,
        )
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))
}