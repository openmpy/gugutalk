package com.pidulgi.server.chat.service

import com.pidulgi.server.chat.dto.response.ChatRoomCreateResponse
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(

    private val chatRoomRepository: ChatRoomRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun create(
        senderId: Long,
        targetId: Long,
    ): ChatRoomCreateResponse {
        memberRepository.findByIdOrNull(targetId)
            ?: throw CustomException("존재하지 않는 회원입니다.")

        val chatRoom = findChatRoom(senderId, targetId)
            ?: chatRoomRepository.save(ChatRoom.of(senderId, targetId))

        return ChatRoomCreateResponse(chatRoom.id)
    }

    private fun findChatRoom(memberA: Long, memberB: Long): ChatRoom? {
        val (m1, m2) = if (memberA < memberB) {
            memberA to memberB
        } else {
            memberB to memberA
        }
        return chatRoomRepository.findByMember1IdAndMember2Id(m1, m2)
    }
}