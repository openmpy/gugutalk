package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetResponse
import com.pidulgi.server.chat.dto.response.ChatRoomGetTargetResponse
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.entity.ChatRoomMember
import com.pidulgi.server.chat.repository.ChatRoomMemberRepository
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.common.dto.CursorResponse
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ChatRoomService(

    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
) {

    @Value("\${s3.endpoint}")
    private lateinit var endpoint: String

    @Transactional
    fun createDirectRoom(
        memberId: Long,
        targetId: Long,
    ): ChatRoomCreateResponse {
        if (memberId == targetId) {
            throw CustomException("자기 자신과 채팅방을 만들 수 없습니다.")
        }

        memberRepository.findByIdOrNull(targetId)
            ?: throw CustomException("존재하지 않는 회원입니다.")

        chatRoomRepository.findDirectRoom(memberId, targetId)
            ?.let { return ChatRoomCreateResponse(it.id) }

        val chatRoom = ChatRoom()
        chatRoomRepository.save(chatRoom)

        val sender = ChatRoomMember(
            chatRoomId = chatRoom.id,
            memberId = memberId,
        )
        val receiver = ChatRoomMember(
            chatRoomId = chatRoom.id,
            memberId = targetId,
        )
        chatRoomMemberRepository.saveAll(listOf(sender, receiver))

        return ChatRoomCreateResponse(chatRoom.id)
    }

    @Transactional(readOnly = true)
    fun gets(
        memberId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int = 20
    ): CursorResponse<ChatRoomGetResponse> {
        val result = chatRoomRepository.findMemberByCursor(memberId, cursorId, cursorDate, size + 1)
            .map {
                ChatRoomGetResponse(
                    chatRoomId = it.chatRoomId,
                    memberId = it.memberId,
                    profileUrl = it.profileKey?.let { key -> "$endpoint$key" },
                    nickname = it.nickname,
                    lastMessage = it.lastMessage,
                    lastMessageAt = it.lastMessageAt,
                )
            }
        val hasNext = result.size > size
        val items = if (hasNext) result.dropLast(1) else result

        println(result)

        return CursorResponse(
            payload = items,
            nextId = if (hasNext) items.last().chatRoomId else null,
            nextDateAt = if (hasNext) items.last().lastMessageAt else null,
            hasNext = hasNext
        )
    }

    @Transactional(readOnly = true)
    fun getTarget(memberId: Long, chatRoomId: Long): ChatRoomGetTargetResponse {
        val members = chatRoomMemberRepository.findAllByChatRoomId(chatRoomId)
        val targetId = members.first { it.memberId != memberId }.memberId

        val target = (memberRepository.findByIdOrNull(targetId)
            ?: throw CustomException("존재하지 않는 회원입니다."))

        return ChatRoomGetTargetResponse(
            memberId = target.id,
            profileUrl = target.profileKey?.let { key -> "$endpoint$key" },
            nickname = target.nickname,
        )
    }
}