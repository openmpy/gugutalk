package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.entity.ChatRoomMember
import com.pidulgi.server.chat.repository.ChatRoomMemberRepository
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(

    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
) {

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

        // 채팅방 생성
        val chatRoom = ChatRoom()
        chatRoomRepository.save(chatRoom)

        // 채팅 회원 생성
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
}