package com.pidulgi.server.member.entity

import com.pidulgi.server.common.jpa.BaseEntity
import jakarta.persistence.*

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
) : BaseEntity()