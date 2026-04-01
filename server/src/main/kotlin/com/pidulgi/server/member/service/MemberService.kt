package com.pidulgi.server.member.service

import com.pidulgi.server.auth.service.AUTH_ACCESS_TOKEN_BLACKLIST_KEY
import com.pidulgi.server.auth.service.AUTH_REFRESH_TOKEN_KEY
import com.pidulgi.server.common.auth.ACCESS_TOKEN_EXPIRE_HOURS
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.discovery.dto.response.MemberDiscoveryResponse
import com.pidulgi.server.member.dto.request.MemberBumpRequest
import com.pidulgi.server.member.dto.request.MemberUpdateCommentRequest
import com.pidulgi.server.member.dto.request.MemberUpdateProfileRequest
import com.pidulgi.server.member.dto.request.MemberUpdateProfileRequest.ProfileImageUpdate
import com.pidulgi.server.member.dto.request.MemberWithdrawRequest
import com.pidulgi.server.member.dto.response.MemberGetMeResponse
import com.pidulgi.server.member.dto.response.MemberGetResponse
import com.pidulgi.server.member.dto.response.MemberImageResponse
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage
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
    private val s3Service: S3Service,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional(readOnly = true)
    fun getMe(memberId: Long): MemberGetMeResponse {
        val member = getMember(memberId)
        val images = memberImageRepository.findAllByMemberIdOrderByTypeAscSortOrderAsc(
            member.id
        )

        val (publicImages, privateImages) = images.partition {
            it.type == ImageType.PUBLIC
        }
        val publicImagesResponse = publicImages.map {
            MemberImageResponse(
                it.id,
                it.sortOrder,
                "$endpoint${it.key}"
            )
        }
        val privateImagesResponse = privateImages.map {
            MemberImageResponse(
                it.id,
                it.sortOrder,
                s3Service.getPresignedUrl(it.key)
            )
        }

        return MemberGetMeResponse(
            member.id,
            publicImagesResponse,
            privateImagesResponse,
            member.nickname,
            member.gender,
            LocalDate.now().year - member.birthYear,
            member.birthYear,
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
    fun updateProfile(memberId: Long, request: MemberUpdateProfileRequest) {
        val member = getMember(memberId)

        if (member.nickname != request.nickname && memberRepository.existsByNickname(request.nickname)) {
            throw CustomException("이미 사용 중인 닉네임입니다.")
        }

        val publicImages = updateImages(member.id, request.publicImages, ImageType.PUBLIC)
        updateImages(member.id, request.privateImages, ImageType.PRIVATE)

        val firstImage = request.publicImages.minByOrNull { it.sortOrder }

        val profileKey = when {
            firstImage == null -> null
            firstImage.imageId != null -> publicImages[firstImage.imageId]?.key
            else -> firstImage.key
        }

        member.updateProfile(profileKey, request.nickname, request.birthYear, request.bio)
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

    private fun updateImages(
        memberId: Long,
        requestImages: List<ProfileImageUpdate>,
        type: ImageType
    ): Map<Long, MemberImage> {
        val images = memberImageRepository.findByMemberIdAndTypeOrderBySortOrder(memberId, type)

        val existing = images.associateBy { it.id }
        val keepIds = requestImages.mapNotNull { it.imageId }.toSet()

        // 삭제
        val deleteTargets = images.filter { it.id !in keepIds }
        val deleteIds = deleteTargets.map { it.id }
        val deleteKeys = deleteTargets.map { it.key }

        if (deleteIds.isNotEmpty()) {
            memberImageRepository.deleteAllByIdIn(deleteIds)
            s3Service.deleteAll(deleteKeys)
        }

        // 순서 변경
        requestImages
            .filter { it.imageId != null }
            .forEach {
                existing[it.imageId]?.sortOrder = it.sortOrder
            }

        // 신규 추가
        val newImages = requestImages
            .filter { it.imageId == null && it.key != null }
            .map {
                MemberImage(
                    memberId = memberId,
                    key = it.key!!,
                    type = type,
                    sortOrder = it.sortOrder
                )
            }

        val savedNewImages = if (newImages.isNotEmpty()) {
            memberImageRepository.saveAll(newImages)
        } else emptyList()

        return (images + savedNewImages).associateBy { it.id }
    }
}