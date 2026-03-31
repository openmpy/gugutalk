package com.pidulgi.server.social.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "blocks", uniqueConstraints = [
        UniqueConstraint(columnNames = ["blocker_id", "blocked_id"])
    ]
)
class Block(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "blocker_id", nullable = false)
    val blockerId: Long,

    @Column(name = "blocked_id", nullable = false)
    val blockedId: Long,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)