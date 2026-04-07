package com.pidulgi.server.admin.entity

import com.pidulgi.server.report.entity.type.ReportType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ban")
class Ban(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "uuid", nullable = false)
    val uuid: String,

    @Column(name = "nickname", nullable = false)
    val nickname: String,

    @Column(name = "phoneNumber", nullable = false)
    val phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ReportType,

    @Column(name = "reason", columnDefinition = "TEXT")
    var reason: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,
) {

    fun update(type: ReportType, reason: String?, expiredAt: LocalDateTime) {
        this.type = type
        this.reason = reason
        this.expiredAt = expiredAt
    }
}