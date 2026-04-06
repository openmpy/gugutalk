package com.pidulgi.server.point.entity

import com.pidulgi.server.point.type.PointSource
import com.pidulgi.server.point.type.TransactionType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "point_transaction")
class PointTransaction(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: TransactionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    val source: PointSource,

    @Column(name = "amount", nullable = false)
    var amount: Long = 0,

    @Column(name = "balance_snapshot", nullable = false)
    var balanceSnapshot: Long = 0,

    @Column(name = "description")
    var description: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)