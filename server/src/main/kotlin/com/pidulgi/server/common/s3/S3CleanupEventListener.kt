package com.pidulgi.server.common.s3

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class S3CleanupEventListener(

    private val s3Service: S3Service
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleS3Cleanup(event: S3CleanupEvent) {
        event.keys.forEach { key ->
            runCatching { s3Service.delete(key) }
                .onFailure { logger.error("S3 삭제 실패 - key: $key", it) }
        }
    }
}