package com.pidulgi.server.social.entity

import com.pidulgi.server.common.jpa.BaseEntity
import jakarta.persistence.*

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
) : BaseEntity()