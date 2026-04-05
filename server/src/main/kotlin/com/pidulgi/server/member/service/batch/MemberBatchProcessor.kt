package com.pidulgi.server.member.service.batch

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberBatchProcessor(

    private val memberRepository: MemberRepository,
    private val likeRepository: LikeRepository,
    private val privateImageGrantRepository: PrivateImageGrantRepository,
    private val blockRepository: BlockRepository
) {

    @Transactional
    fun processBatch(members: List<Member>) {
        val memberIds = members.map { it.id }

        likeRepository.hardDeleteAllByMemberIdIn(memberIds)
        privateImageGrantRepository.hardDeleteAllByMemberIdIn(memberIds)
        blockRepository.hardDeleteAllByMemberIdIn(memberIds)
        memberRepository.hardDeleteByIdIn(memberIds)
    }
}