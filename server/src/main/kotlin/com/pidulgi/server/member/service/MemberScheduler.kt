package com.pidulgi.server.member.service

import com.pidulgi.server.common.s3.S3Service
import com.pidulgi.server.member.entity.type.ImageStatus.PENDING
import com.pidulgi.server.member.repository.MemberImageRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MemberScheduler(

    private val memberImageRepository: MemberImageRepository,
    private val s3Service: S3Service,
) {

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
}