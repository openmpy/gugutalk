package com.pidulgi.server.report.entity

import com.pidulgi.server.report.entity.type.ReportImageStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "report_image")
class ReportImage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "report_id", nullable = false)
    var reportId: Long = 0,

    @Column(name = "key", nullable = false)
    val key: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportImageStatus = ReportImageStatus.PENDING,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {

    fun upload(reportId: Long) {
        this.reportId = reportId
        this.status = ReportImageStatus.COMPLETE
    }
}