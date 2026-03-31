package com.pidulgi.server.member.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "private_image_grants", uniqueConstraints = [
        UniqueConstraint(columnNames = ["granter_id", "grantee_id"])
    ]
)
class PrivateImageGrant(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "granter_id", nullable = false)
    val granterId: Long,

    @Column(name = "grantee_id", nullable = false)
    val granteeId: Long,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)