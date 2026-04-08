package com.pidulgi.server.admin.service

import com.pidulgi.server.admin.dto.request.BanAddRequest
import com.pidulgi.server.admin.dto.response.BanGetDetailResponse
import com.pidulgi.server.admin.dto.response.BanGetMemberResponse
import com.pidulgi.server.admin.dto.response.BanGetResponse
import com.pidulgi.server.admin.entity.Ban
import com.pidulgi.server.admin.repository.BanRepository
import com.pidulgi.server.common.dto.PageResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BanService(

    private val banRepository: BanRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun add(request: BanAddRequest) {
        val ban = Ban(
            uuid = request.uuid,
            nickname = request.nickname,
            phoneNumber = request.phoneNumber,
            type = request.type,
            reason = request.reason,
            expiredAt = LocalDateTime.now().plusDays(request.day),
        )
        banRepository.save(ban)
    }

    @Transactional
    fun remove(banId: Long) {
        val ban = (banRepository.findByIdOrNull(banId)
            ?: throw CustomException("존재하지 않는 정지입니다."))

        banRepository.delete(ban)
    }

    @Transactional(readOnly = true)
    fun gets(page: Int, size: Int): PageResponse<BanGetResponse> {
        val offset = page * size
        val result = banRepository.findAllByPage(offset, size + 1)
            .map {
                BanGetResponse(
                    banId = it.id,
                    type = it.type,
                    uuid = it.uuid,
                    nickname = it.nickname,
                    reason = it.reason,
                    expiredAt = it.expiredAt
                )
            }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return PageResponse(
            payload = items,
            page = page,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun get(banId: Long): BanGetDetailResponse {
        val ban = banRepository.findByIdOrNull(banId)
            ?: throw CustomException("존재하지 않는 정지입니다.")

        return BanGetDetailResponse(
            banId = ban.id,
            type = ban.type,
            uuid = ban.uuid,
            nickname = ban.nickname,
            phoneNumber = ban.phoneNumber,
            reason = ban.reason,
            createdAt = ban.createdAt,
            expiredAt = ban.expiredAt
        )
    }

    @Transactional(readOnly = true)
    fun getMemberByUuid(uuid: String): BanGetMemberResponse {
        val member = memberRepository.findByUuid(uuid)

        return member?.let { m ->
            BanGetMemberResponse(
                nickname = m.nickname,
                phoneNumber = m.phoneNumber.value,
            )
        } ?: BanGetMemberResponse(
            nickname = null,
            phoneNumber = null,
        )
    }

    @Transactional(readOnly = true)
    fun search(
        type: String,
        keyword: String,
        page: Int,
        size: Int
    ): PageResponse<BanGetResponse> {
        val offset = page * size
        val result = if (type.equals("uuid", ignoreCase = true)) {
            banRepository.findAllByUuidPage(keyword, offset, size + 1)
        } else {
            banRepository.findAllByNicknamePage(keyword, offset, size + 1)
        }.map {
            BanGetResponse(
                banId = it.id,
                type = it.type,
                uuid = it.uuid,
                nickname = it.nickname,
                reason = it.reason,
                expiredAt = it.expiredAt
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        return PageResponse(
            payload = items,
            page = page,
            hasNext = hasNext
        )
    }
}