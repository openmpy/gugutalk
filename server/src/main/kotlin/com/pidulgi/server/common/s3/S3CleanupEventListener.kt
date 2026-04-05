package com.pidulgi.server.common.s3

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class S3CleanupEventListener(

    private val s3Service: S3Service
) {

    private val log = KotlinLogging.logger {}

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleS3Cleanup(event: S3CleanupEvent) {
        runCatching { s3Service.deleteAll(event.keys) }
            .onFailure { log.error(it) { "S3 삭제 배치 작업 실패 - keys: ${event.keys}" } }
    }
}