package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.event.ChatEvent
import com.pidulgi.server.chat.dto.event.ChatRoomDeleteEvent
import com.pidulgi.server.chat.dto.event.type.ChatEventType.DELETE_CHAT_ROOM
import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.dto.response.ChatRoomSearchResponse
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.chat.service.command.ChatRoomCreateCommand
import com.pidulgi.server.chat.service.event.ChatQueueEvent
import com.pidulgi.server.chat.service.event.ChatTopicEvent
import com.pidulgi.server.chat.service.extension.toChatRoomGetResponse
import com.pidulgi.server.chat.service.extension.toChatRoomSearchResponse
import com.pidulgi.server.chat.service.query.GetsChatRoomQuery
import com.pidulgi.server.chat.service.query.SearchChatRoomQuery
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.dto.CursorSimilarityResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.point.entity.type.PointSource
import com.pidulgi.server.point.repository.PointRepository
import com.pidulgi.server.social.repository.BlockRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(

    @Value("\${s3.endpoint}") private val endpoint: String,

    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val pointRepository: PointRepository,
    private val blockRepository: BlockRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun create(command: ChatRoomCreateCommand): ChatRoomCreateResponse {
        memberRepository.findByIdOrNull(command.targetId)
            ?: throw CustomException("존재하지 않는 회원입니다.")

        if (blockRepository.existsBlock(command.senderId, command.targetId)) {
            throw CustomException("차단된 회원입니다.")
        }

        val point = (pointRepository.findByMemberId(command.senderId)
            ?: throw CustomException("포인트 정보를 찾을 수 없습니다."))

        if (point.balance < PointSource.SEND_MESSAGE.point) {
            throw CustomException("포인트가 부족합니다.")
        }

        val chatRoom = findChatRoom(command.senderId, command.targetId)
            ?: chatRoomRepository.save(ChatRoom.of(command.senderId, command.targetId))

        point.use(PointSource.SEND_MESSAGE.point)
        return ChatRoomCreateResponse(chatRoom.id)
    }

    @Transactional
    fun delete(
        memberId: Long,
        chatRoomId: Long
    ) {
        val chatRoom = (chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다."))

        check(chatRoom.hasMember(memberId)) { "접근할 수 없는 채팅방입니다." }

        val targetId = getTargetId(chatRoom, memberId)

        chatRoom.delete()

        // 이벤트
        val chatEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            null
        )
        applicationEventPublisher.publishEvent(ChatTopicEvent(chatRoomId, chatEvent))

        val chatRoomEvent = ChatEvent(
            DELETE_CHAT_ROOM,
            ChatRoomDeleteEvent(
                chatRoomId,
            )
        )
        applicationEventPublisher.publishEvent(ChatQueueEvent(targetId, chatRoomEvent))
    }

    @Transactional(readOnly = true)
    fun gets(query: GetsChatRoomQuery): CursorResponse<ChatRoomGetResponse> {
        val result = chatRoomRepository.findAllChatRoomsByCursor(
            memberId = query.memberId,
            status = query.status,
            cursorId = query.cursorId,
            cursorDate = query.cursorDate,
            size = query.size + 1
        ).map { it.toChatRoomGetResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorResponse(
            payload = items,
            nextId = items.lastOrNull()?.chatRoomId,
            nextDateAt = items.lastOrNull()?.sortAt,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun search(query: SearchChatRoomQuery): CursorSimilarityResponse<ChatRoomSearchResponse> {
        val result = chatRoomRepository.findAllChatRoomsByNicknameByCursor(
            memberId = query.memberId,
            nickname = query.nickname,
            cursorId = query.cursorId,
            cursorSimilarity = query.cursorSimilarity,
            size = query.size + 1
        ).map { it.toChatRoomSearchResponse(endpoint) }

        val hasNext = result.size > query.size
        val items = if (hasNext) result.dropLast(1) else result

        return CursorSimilarityResponse(
            payload = items,
            nextId = items.lastOrNull()?.chatRoomId,
            nextSimilarity = items.lastOrNull()?.similarityScore,
            hasNext = hasNext
        )
    }

    @Transactional
    fun markAsRead(memberId: Long, chatRoomId: Long) {
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다.")

        if (chatRoom.member1Id != memberId && chatRoom.member2Id != memberId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        val lastMessageId = messageRepository.findLastMessageId(chatRoomId)
            ?: return

        chatRoom.read(memberId, lastMessageId)
    }

    private fun findChatRoom(memberA: Long, memberB: Long): ChatRoom? {
        val (m1, m2) = if (memberA < memberB) {
            memberA to memberB
        } else {
            memberB to memberA
        }
        return chatRoomRepository.findByMember1IdAndMember2Id(m1, m2)
    }

    private fun getTargetId(chatRoom: ChatRoom, memberId: Long): Long = if (chatRoom.member1Id == memberId) {
        chatRoom.member2Id
    } else {
        chatRoom.member1Id
    }
}