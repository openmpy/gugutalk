package com.pidulgi.server.report.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "report_image")
class ReportImage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "report_id", nullable = false)
    val reportId: Long,

    @Column(name = "key", nullable = false)
    val key: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)