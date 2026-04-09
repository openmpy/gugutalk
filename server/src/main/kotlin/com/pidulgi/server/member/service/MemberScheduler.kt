package com.pidulgi.server.member.service

import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.entity.type.ImageStatus.PENDING
import com.pidulgi.server.member.repository.MemberImageRepository
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.service.batch.MemberBatchProcessor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MemberScheduler(

    private val memberRepository: MemberRepository,
    private val memberImageRepository: MemberImageRepository,
    private val memberBatchProcessor: MemberBatchProcessor,
    private val s3Service: S3Service,
) {

    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpPendingImages() {
        val expiredBefore = LocalDateTime.now().minusHours(24)
        val pendingImages = memberImageRepository.findByStatusAndCreatedAtBefore(
            PENDING, expiredBefore
        )

        if (pendingImages.isEmpty()) {
            return
        }

        memberImageRepository.deleteAllByIdInBatch(pendingImages.map { it.id })
        s3Service.deleteAll(pendingImages.map { it.key })
    }

    @Scheduled(cron = "0 0 9 * * *")
    fun cleanUpMembers() {
        val expiredBefore = LocalDateTime.now().minusDays(7)
        val deletedMembers = memberRepository.findAllByDeleted(expiredBefore)

        if (deletedMembers.isEmpty()) {
            return
        }

        deletedMembers.chunked(100).forEach { batch ->
            try {
                memberBatchProcessor.processBatch(batch)
            } catch (e: Exception) {
                log.error(e) { "회원 삭제 배치 작업 실패 - ${batch.map { it.id }}" }
            }
        }
    }
}