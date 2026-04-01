package com.pidulgi.server.report.entity

import com.pidulgi.server.report.entity.type.ReportStatus
import com.pidulgi.server.report.entity.type.ReportType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "report")
class Report(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "reporter_id", nullable = false)
    val reporterId: Long,

    @Column(name = "reported_id", nullable = false)
    val reportedId: Long,

    @Column(name = "reporter_nickname", nullable = false)
    val reporterNickname: String,

    @Column(name = "reported_nickname", nullable = false)
    val reportedNickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: ReportStatus = ReportStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: ReportType,

    @Column(name = "reason", columnDefinition = "TEXT")
    val reason: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)