package com.pidulgi.server.member.service

import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.member.service.command.PrivateImageGrantCloseCommand
import com.pidulgi.server.member.service.command.PrivateImageGrantOpenCommand
import com.pidulgi.server.member.service.extension.toSettingResponse
import com.pidulgi.server.member.service.query.GetGrantedMembersQuery
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrivateImageGrantService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val privateImageGrantRepository: PrivateImageGrantRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun open(command: PrivateImageGrantOpenCommand) {
        if (!memberRepository.existsById(command.granteeId)) {
            throw CustomException("존재하지 않는 회원입니다.")
        }
        if (privateImageGrantRepository.existsByGranterIdAndGranteeId(command.granterId, command.granteeId)) {
            throw CustomException("이미 공개한 대상입니다.")
        }

        val privateImageGrant = PrivateImageGrant(
            granterId = command.granterId,
            granteeId = command.granteeId,
        )
        privateImageGrantRepository.save(privateImageGrant)
    }

    @Transactional(readOnly = true)
    fun getGrantedMembers(query: GetGrantedMembersQuery): CursorResponse<SettingResponse> {
        val result = privateImageGrantRepository.findGrantsByCursor(
            query.granterId, query.cursorId, query.size + 1
        ).map { it.toSettingResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.id,
            nextDateAt = null,
            hasNext = hasNext
        )
    }

    @Transactional
    fun close(command: PrivateImageGrantCloseCommand) {
        val privateImageGrant = (privateImageGrantRepository.findByGranterIdAndGranteeId(
            command.granterId, command.granteeId
        ) ?: throw CustomException("공개한 대상이 아닙니다."))

        privateImageGrantRepository.delete(privateImageGrant)
    }
}