package com.pidulgi.server.social.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.repository.BlockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlockService(

    private val blockRepository: BlockRepository,
) {

    @Transactional
    fun add(blockerId: Long, blockedId: Long) {
        if (blockedId != blockerId) {
            throw CustomException("자기 자신을 차단할 수 없습니다.")
        }
        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw CustomException("이미 차단한 대상입니다.")
        }

        val block = Block(
            blockerId = blockerId,
            blockedId = blockedId,
        )
        blockRepository.save(block)
    }

    @Transactional
    fun remove(blockerId: Long, blockedId: Long) {
        val block = (blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
            ?: throw CustomException("차단을 한 적이 없습니다."))

        blockRepository.delete(block)
    }
}