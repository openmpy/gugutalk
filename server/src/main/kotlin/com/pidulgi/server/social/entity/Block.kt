package com.pidulgi.server.social.entity

import com.pidulgi.server.common.jpa.BaseEntity
import jakarta.persistence.*

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
) : BaseEntity()