package com.pidulgi.server.social.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "likes", uniqueConstraints = [
        UniqueConstraint(columnNames = ["liker_id", "liked_id"])
    ]
)
class Like(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "liker_id", nullable = false)
    val likerId: Long,

    @Column(name = "liked_id", nullable = false)
    val likedId: Long,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)