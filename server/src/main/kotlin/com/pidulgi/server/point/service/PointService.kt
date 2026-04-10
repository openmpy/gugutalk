package com.pidulgi.server.point.service

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.point.dto.response.PointGetResponse
import com.pidulgi.server.point.entity.Point
import com.pidulgi.server.point.entity.PointTransaction
import com.pidulgi.server.point.entity.type.PointSource
import com.pidulgi.server.point.entity.type.TransactionType
import com.pidulgi.server.point.repository.PointRepository
import com.pidulgi.server.point.repository.PointTransactionRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PointService(

    private val pointRepository: PointRepository,
    private val pointTransactionRepository: PointTransactionRepository,
    private val redisTemplate: StringRedisTemplate
) {

    companion object {
        private const val POINT_EARN_ATTENDANCE_KEY = "point:attendance:"
        private const val POINT_EARN_AD_REWARD_KEY = "point:ad-reward:"

        private const val AD_REWARD_DELAY_HOURS = 3L
    }

    @Transactional
    fun earnByAttendance(memberId: Long) {
        val point = getPoint(memberId)

        val attendanceKey = POINT_EARN_ATTENDANCE_KEY + memberId
        redisTemplate.opsForValue().get(attendanceKey)?.let {
            throw CustomException("오늘은 이미 출석 체크를 완료하셨습니다.")
        }

        // 포인트 지급
        point.earn(PointSource.ATTENDANCE.point)

        // 포인트 트랜잭션 생성
        val pointTransaction = PointTransaction(
            memberId = memberId,
            type = TransactionType.EARN,
            source = PointSource.ATTENDANCE,
            amount = PointSource.ATTENDANCE.point,
            balanceSnapshot = point.balance,
            description = PointSource.ATTENDANCE.description
        )
        pointTransactionRepository.save(pointTransaction)

        // 포인트 지급 딜레이 저장
        val midnight = LocalDate.now().plusDays(1).atStartOfDay()
        val ttl = Duration.between(LocalDateTime.now(), midnight)
        redisTemplate.opsForValue().set(attendanceKey, "1", ttl)
    }

    @Transactional
    fun earnByAdReward(memberId: Long) {
        val point = getPoint(memberId)

        val adRewardKey = POINT_EARN_AD_REWARD_KEY + memberId
        redisTemplate.opsForValue().get(adRewardKey)?.let {
            throw CustomException("광고 보상은 ${AD_REWARD_DELAY_HOURS}시간마다 받을 수 있습니다.")
        }

        // 포인트 지급
        point.earn(PointSource.AD_REWARD.point)

        // 포인트 트랜잭션 생성
        val pointTransaction = PointTransaction(
            memberId = memberId,
            type = TransactionType.EARN,
            source = PointSource.AD_REWARD,
            amount = PointSource.AD_REWARD.point,
            balanceSnapshot = point.balance,
            description = PointSource.AD_REWARD.description
        )
        pointTransactionRepository.save(pointTransaction)

        // 포인트 지급 딜레이 저장
        val ttl = Duration.ofHours(AD_REWARD_DELAY_HOURS)
        redisTemplate.opsForValue().set(adRewardKey, "1", ttl)
    }

    @Transactional(readOnly = true)
    fun get(memberId: Long): PointGetResponse {
        val point = getPoint(memberId)
        return PointGetResponse(point.balance)
    }

    private fun getPoint(memberId: Long): Point = (pointRepository.findByMemberId(memberId)
        ?: throw CustomException("존재하지 않는 포인트 정보입니다."))
}