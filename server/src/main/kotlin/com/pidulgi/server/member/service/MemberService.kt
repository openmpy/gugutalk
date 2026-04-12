package com.pidulgi.server.member.service

import com.pidulgi.server.auth.service.AUTH_ACCESS_TOKEN_BLACKLIST_KEY
import com.pidulgi.server.auth.service.AUTH_REFRESH_TOKEN_KEY
import com.pidulgi.server.common.dto.CursorSimilarityResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.common.util.AgeCalculator
import com.pidulgi.server.member.dto.request.MemberBumpRequest
import com.pidulgi.server.member.dto.request.MemberUpdateCommentRequest
import com.pidulgi.server.member.dto.request.MemberUpdateProfileRequest
import com.pidulgi.server.member.dto.request.MemberUpdateProfileRequest.ProfileImageUpdate
import com.pidulgi.server.member.dto.request.MemberWithdrawRequest
import com.pidulgi.server.member.dto.response.*
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.ImageType
import com.pidulgi.server.member.entity.vo.MemberBio
import com.pidulgi.server.member.entity.vo.MemberBirthYear
import com.pidulgi.server.member.entity.vo.MemberComment
import com.pidulgi.server.member.entity.vo.MemberNickname
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.member.service.event.MemberWithdrawEvent
import com.pidulgi.server.member.service.extension.toMemberDiscoveryResponse
import com.pidulgi.server.member.service.query.SearchByNicknameQuery
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class MemberService(

    @Value("\${s3.endpoint}") private val endpoint: String,
    @Value("\${jwt.access-token-expire-seconds}") private val accessTokenExpireSeconds: Long,

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val likeRepository: LikeRepository,
    private val blockRepository: BlockRepository,
    private val privateImageGrantRepository: PrivateImageGrantRepository,
    private val redisTemplate: StringRedisTemplate,
    private val s3Service: S3Service,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

    @Transactional(readOnly = true)
    fun getMe(memberId: Long): MemberGetMeResponse {
        val member = getMember(memberId)
        val memberImages = memberImageRepository.findAllByMemberId(memberId).sortedBy { it.sortOrder }

        val (publicImages, privateImages) = memberImages.partition { it.type == ImageType.PUBLIC }
        val publicImagesResponse = publicImages.map {
            MemberImageResponse(it.id, it.sortOrder, "$endpoint${it.key}")
        }
        val privateImagesResponse = privateImages.map {
            MemberImageResponse(it.id, it.sortOrder, s3Service.getPresignedUrl(it.key))
        }

        val likes = likeRepository.countByLikedId(memberId)

        return MemberGetMeResponse(
            member.id,
            publicImagesResponse,
            privateImagesResponse,
            member.nickname.value,
            member.gender,
            AgeCalculator.calculate(member.birthYear.value),
            member.birthYear.value,
            member.bio?.value,
            likes
        )
    }

    @Transactional(readOnly = true)
    fun getMember(memberId: Long, targetId: Long): MemberGetResponse {
        val member = getMember(memberId)
        val target = getMember(targetId)
        val memberImages = memberImageRepository.findAllByMemberIdAndTypeOrderBySortOrder(
            target.id, ImageType.PUBLIC
        ).map {
            MemberImageResponse(it.id, it.sortOrder, endpoint + it.key)
        }

        val isLiked = likeRepository.existsByLikerIdAndLikedId(memberId, targetId)
        val isBlocked = blockRepository.existsByBlockerIdAndBlockedId(memberId, targetId)
        val isPrivateImageGranted = privateImageGrantRepository.existsByGranterIdAndGranteeId(memberId, targetId)
        val isPrivateImageGrantedByTarget = privateImageGrantRepository.existsByGranterIdAndGranteeId(
            targetId, memberId
        )
        val likes = likeRepository.countByLikedId(targetId)
        val distance = memberRepository.findDistanceFromLocation(member.location, targetId)

        return MemberGetResponse(
            memberId = target.id,
            images = memberImages,
            nickname = target.nickname.value,
            gender = target.gender,
            age = AgeCalculator.calculate(target.birthYear.value),
            bio = target.bio?.value,
            likes = likes,
            distance = distance?.div(1000),
            updatedAt = target.updatedAt,
            isChatEnabled = target.isChatEnabled,
            isLiked = isLiked,
            isBlocked = isBlocked,
            isPrivateImageGranted = isPrivateImageGranted,
            isPrivateImageGrantedByTarget = isPrivateImageGrantedByTarget,
        )
    }

    @Transactional
    fun withdraw(memberId: Long, request: MemberWithdrawRequest) {
        val member = getMember(memberId)

        val accessTokenBlacklistKey = AUTH_ACCESS_TOKEN_BLACKLIST_KEY + request.accessToken
        val refreshTokenKey = AUTH_REFRESH_TOKEN_KEY + request.refreshToken
        val refreshTokenKeyExists = redisTemplate.hasKey(refreshTokenKey)

        if (refreshTokenKeyExists == false) {
            throw CustomException("로그인을 다시 해주시길 바랍니다.")
        }

        redisTemplate.opsForValue().set(
            accessTokenBlacklistKey,
            memberId.toString(),
            Duration.ofSeconds(accessTokenExpireSeconds)
        )
        redisTemplate.delete(refreshTokenKey)

        member.withdraw()

        applicationEventPublisher.publishEvent(MemberWithdrawEvent(memberId))
    }

    @Transactional
    fun bump(memberId: Long, request: MemberBumpRequest) {
        val member = getMember(memberId)

        if (request.longitude != null && request.latitude != null) {
            val point = GEOMETRY_FACTORY.createPoint(Coordinate(request.longitude, request.latitude))
            member.bump(point)
        }
    }

    @Transactional
    fun updateProfile(memberId: Long, request: MemberUpdateProfileRequest) {
        val memberNickname = MemberNickname(request.nickname)
        val memberBirthYear = MemberBirthYear(request.birthYear)
        val memberBio = request.bio?.let { MemberBio(it) }

        val member = getMember(memberId)

        if (member.nickname.value != request.nickname && memberRepository.existsByNickname(memberNickname)) {
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

        member.updateProfile(profileKey, memberNickname, memberBirthYear, memberBio)
    }

    @Transactional
    fun updateComment(memberId: Long, request: MemberUpdateCommentRequest) {
        val member = getMember(memberId)
        val memberComment = MemberComment(request.comment)

        member.updateComment(memberComment)
    }

    @Transactional(readOnly = true)
    fun searchByNickname(query: SearchByNicknameQuery): CursorSimilarityResponse<MemberSearchResponse> {
        val member = getMember(query.memberId)
        val result = memberRepository.findAllMembersByNicknameWithCursor(
            query.memberId,
            query.nickname,
            member.location,
            query.cursorId,
            query.cursorSimilarity,
            query.size + 1
        ).map { it.toMemberDiscoveryResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorSimilarityResponse(
            payload = items,
            nextId = items.lastOrNull()?.memberId,
            nextSimilarity = items.lastOrNull()?.similarityScore,
            hasNext = hasNext,
        )
    }

    @Transactional(readOnly = true)
    fun getChatEnabled(memberId: Long): MemberGetChatEnabledResponse {
        val member = getMember(memberId)
        return MemberGetChatEnabledResponse(member.isChatEnabled)
    }

    @Transactional
    fun toggleChatEnabled(memberId: Long): MemberGetChatEnabledResponse {
        val member = getMember(memberId)
        member.toggleChatEnabled()
        return MemberGetChatEnabledResponse(member.isChatEnabled)
    }

    private fun getMember(memberId: Long): Member = (memberRepository.findByIdOrNull(memberId)
        ?: throw CustomException("존재하지 않는 회원입니다."))

    private fun updateImages(
        memberId: Long,
        requestImages: List<ProfileImageUpdate>,
        type: ImageType
    ): Map<Long, MemberImage> {
        val images = memberImageRepository.findAllByMemberIdAndTypeOrderBySortOrder(memberId, type)

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
        val newKeys = requestImages
            .filter { it.imageId == null && it.key != null }
            .map { it.key!! }

        val savedNewImages = if (newKeys.isNotEmpty()) {
            val pendingImages = memberImageRepository.findAllByKeyIn(newKeys)

            pendingImages.forEach { image ->
                val matched = requestImages.first { it.key == image.key }
                image.upload(memberId, matched.sortOrder)
            }
            memberImageRepository.saveAll(pendingImages)
        } else emptyList()

        return (images + savedNewImages).associateBy { it.id }
    }
}