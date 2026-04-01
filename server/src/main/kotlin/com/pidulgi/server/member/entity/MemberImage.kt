package com.pidulgi.server.member.entity

import com.pidulgi.server.member.entity.type.ImageType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_image")
class MemberImage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "key", nullable = false)
    val key: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: ImageType,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)