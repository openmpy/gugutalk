package com.pidulgi.server.social.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomDeleteEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.SettingResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.repository.BlockRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class BlockService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val blockRepository: BlockRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @Transactional
    fun add(blockerId: Long, blockedId: Long) {
        if (blockedId == blockerId) {
            throw CustomException("자기 자신을 차단할 수 없습니다.")
        }
        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw CustomException("이미 차단한 대상입니다.")
        }

        val block = Block(
            blockerId = blockerId,
            blockedId = blockedId,
        )
        blockRepository.save(block)

        val (member1Id, member2Id) = if (blockerId < blockedId) blockerId to blockedId else blockedId to blockerId
        val chatRoom = chatRoomRepository.findByMember1IdAndMember2Id(member1Id, member2Id)

        chatRoom?.let {
            it.delete()

            val event = ChatEvent(
                DELETE_CHAT_ROOM,
                null
            )
            messagingTemplate.convertAndSend(
                "/topic/chat-rooms/${chatRoom.id}",
                event
            )

            val chatRoomEvent = ChatEvent(
                DELETE_CHAT_ROOM,
                ChatRoomDeleteEvent(
                    chatRoom.id,
                )
            )
            messagingTemplate.convertAndSendToUser(
                blockedId.toString(),
                "/queue/chat-rooms",
                chatRoomEvent
            )
        }
    }

    @Transactional
    fun remove(blockerId: Long, blockedId: Long) {
        val block = (blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
            ?: throw CustomException("차단을 한 적이 없습니다."))

        blockRepository.delete(block)
    }

    @Transactional(readOnly = true)
    fun getBlockedMembers(
        blockerId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int = 20
    ): CursorResponse<SettingResponse> {
        val result = blockRepository.findBlocksByCursor(blockerId, cursorId, cursorDate, size + 1)
            .map {
                SettingResponse(
                    id = it.blockId,
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