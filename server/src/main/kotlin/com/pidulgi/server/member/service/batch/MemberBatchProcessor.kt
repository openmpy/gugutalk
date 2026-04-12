package com.pidulgi.server.member.service.batch

import com.pidulgi.server.common.s3.event.S3CleanupEvent
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.point.repository.PointRepository
import com.pidulgi.server.point.repository.PointTransactionRepository
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberBatchProcessor(

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val likeRepository: LikeRepository,
    private val privateImageGrantRepository: PrivateImageGrantRepository,
    private val blockRepository: BlockRepository,
    private val pointRepository: PointRepository,
    private val pointTransactionRepository: PointTransactionRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun processBatch(members: List<Member>) {
        val memberIds = members.map { it.id }
        val memberImages = memberImageRepository.findAllByMemberIdIn(memberIds)

        likeRepository.deleteAllByMemberIds(memberIds)
        privateImageGrantRepository.deleteAllByMemberIds(memberIds)
        blockRepository.deleteAllByMemberIds(memberIds)
        pointRepository.deleteAllByMemberIds(memberIds)
        pointTransactionRepository.deleteAllByMemberIds(memberIds)
        memberImageRepository.deleteAllInBatch(memberImages)
        memberRepository.hardDeleteByIdIn(memberIds)

        applicationEventPublisher.publishEvent(S3CleanupEvent(memberImages.map { it.key }))
    }
}