package com.pidulgi.server.social.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomDeleteEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.service.event.ChatQueueEvent
import com.pidulgi.server.chat.service.event.ChatTopicEvent
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.service.command.BlockAddMemberCommand
import com.pidulgi.server.social.service.command.BlockRemoveMemberCommand
import com.pidulgi.server.social.service.extension.toSettingResponse
import com.pidulgi.server.social.service.query.GetBlockedMembersQuery
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlockService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val blockRepository: BlockRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun add(command: BlockAddMemberCommand) {
        if (!memberRepository.existsById(command.blockedId)) {
            throw CustomException("존재하지 않는 회원입니다.")
        }
        if (blockRepository.existsByBlockerIdAndBlockedId(command.blockerId, command.blockedId)) {
            throw CustomException("이미 차단한 대상입니다.")
        }

        val block = Block(blockerId = command.blockerId, blockedId = command.blockedId)
        blockRepository.save(block)

        val (member1Id, member2Id) = if (command.blockerId < command.blockedId)
            command.blockerId to command.blockedId else command.blockedId to command.blockerId
        val chatRoom = chatRoomRepository.findByMember1IdAndMember2Id(member1Id, member2Id)
            ?: throw CustomException("존재하지 않는 채팅방입니다.")

        chatRoom.delete()

        // 이벤트
        val chatEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            null
        )
        applicationEventPublisher.publishEvent(ChatTopicEvent(chatRoom.id, chatEvent))

        val chatRoomEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            ChatRoomDeleteEvent(
                chatRoom.id,
            )
        )
        applicationEventPublisher.publishEvent(ChatQueueEvent(command.blockedId, chatRoomEvent))
    }

    @Transactional(readOnly = true)
    fun getBlockedMembers(query: GetBlockedMembersQuery): CursorResponse<SettingResponse> {
        val result = blockRepository.findAllBlocksByCursor(query.blockerId, query.cursorId, query.size + 1)
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

    @Transactional
    fun remove(command: BlockRemoveMemberCommand) {
        val block = (blockRepository.findByBlockerIdAndBlockedId(command.blockerId, command.blockedId)
            ?: throw CustomException("차단한 대상이 아닙니다."))

        blockRepository.delete(block)
    }
}