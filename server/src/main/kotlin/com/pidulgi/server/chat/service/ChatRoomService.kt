package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.chat.service.command.ChatRoomCreateCommand
import com.pidulgi.server.chat.service.event.ChatDeleteEvent
import com.pidulgi.server.common.dto.CursorResponse
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
import java.time.LocalDateTime

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
        blockRepository.findBlock(command.senderId, command.targetId)
            ?.let { throw CustomException("차단된 회원입니다.") }

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

        if (chatRoom.member1Id != memberId && chatRoom.member2Id != memberId) {
            throw CustomException("접근할 수 없는 채팅방입니다.")
        }

        val targetId = getTargetId(chatRoom, memberId)

        chatRoom.delete()

        applicationEventPublisher.publishEvent(ChatDeleteEvent(chatRoomId, targetId))
    }

    @Transactional(readOnly = true)
    fun gets(
        memberId: Long,
        status: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): CursorResponse<ChatRoomGetResponse> {
        val result = chatRoomRepository.findChatRoomsByCursor(
            memberId = memberId,
            status = status,
            cursorId = cursorId,
            cursorDate = cursorDate,
            size = size + 1
        ).map {
            ChatRoomGetResponse(
                chatRoomId = it.chatRoomId,
                targetId = it.targetId,
                nickname = it.nickname,
                profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                lastMessage = it.lastMessage,
                lastMessageAt = it.lastMessageAt,
                sortAt = it.sortAt,
                unreadCount = it.unreadCount,
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result
        val last = items.lastOrNull()

        return CursorResponse(
            payload = items,
            nextId = last?.chatRoomId,
            nextDateAt = last?.sortAt,
            hasNext = hasNext
        )
    }

    @Transactional
    fun markAsRead(memberId: Long, chatRoomId: Long) {
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException("존재하지 않는 채팅방입니다.")

        val lastMessageId = messageRepository.findLastMessageId(chatRoomId)
            ?: return

        chatRoom.read(memberId, lastMessageId)
    }

    @Transactional(readOnly = true)
    fun search(
        memberId: Long,
        keyword: String,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): CursorResponse<ChatRoomGetResponse> {
        if (keyword.length < 2) {
            throw CustomException("검색어는 2자 이상이어야 합니다.")
        }

        val result = chatRoomRepository.searchChatRoomsByCursor(
            memberId = memberId,
            keyword = keyword,
            cursorId = cursorId,
            cursorDate = cursorDate,
            size = size + 1
        ).map {
            ChatRoomGetResponse(
                chatRoomId = it.chatRoomId,
                targetId = it.targetId,
                nickname = it.nickname,
                profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                lastMessage = it.lastMessage,
                lastMessageAt = it.lastMessageAt,
                sortAt = it.sortAt,
                unreadCount = it.unreadCount,
            )
        }

        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result
        val last = items.lastOrNull()

        return CursorResponse(
            payload = items,
            nextId = last?.chatRoomId,
            nextDateAt = last?.sortAt,
            hasNext = hasNext
        )
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