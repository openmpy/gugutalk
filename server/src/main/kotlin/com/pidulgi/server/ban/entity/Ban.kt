package com.pidulgi.server.ban.entity

import com.pidulgi.server.common.jpa.BaseEntity
import com.pidulgi.server.report.entity.type.ReportType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ban")
class Ban(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "uuid", unique = true, nullable = false)
    val uuid: String,

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: ReportType,

    @Column(name = "reason")
    val reason: String?,

    @Column(name = "expired_at", nullable = false)
    val expiredAt: LocalDateTime,
) : BaseEntity()