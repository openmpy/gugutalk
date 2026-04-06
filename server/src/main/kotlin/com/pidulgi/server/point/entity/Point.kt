package com.pidulgi.server.point.entity

import com.pidulgi.server.common.exception.CustomException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "point")
class Point(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member_id", unique = true, nullable = false)
    val memberId: Long,

    @Column(name = "balance", nullable = false)
    var balance: Long = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    fun earn(amount: Long) {
        if (amount <= 0) {
            throw CustomException("적립 포인트는 0보다 커야 합니다.")
        }

        this.balance += amount
        this.updatedAt = LocalDateTime.now()
    }

    fun use(amount: Long) {
        if (amount <= 0) {
            throw CustomException("사용 포인트는 0보다 커야 합니다.")
        }
        if (this.balance < amount) {
            throw CustomException("포인트가 부족합니다.")
        }

        this.balance -= amount
        this.updatedAt = LocalDateTime.now()
    }
}