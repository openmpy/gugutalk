package com.pidulgi.server.report.entity

import com.pidulgi.server.common.jpa.BaseEntity
import com.pidulgi.server.report.entity.type.ReportStatus
import com.pidulgi.server.report.entity.type.ReportType
import jakarta.persistence.*

@Entity
@Table(name = "report")
class Report(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "reporter_id", nullable = false)
    val reporterId: Long,

    @Column(name = "reporter_uuid", nullable = false)
    val reporterUuid: String,

    @Column(name = "reporter_phone_number", nullable = false)
    val reporterPhoneNumber: String,

    @Column(name = "reporter_nickname", nullable = false)
    val reporterNickname: String,

    @Column(name = "reported_id", nullable = false)
    val reportedId: Long,

    @Column(name = "reported_uuid", nullable = false)
    val reportedUuid: String,

    @Column(name = "reported_phone_number", nullable = false)
    val reportedPhoneNumber: String,

    @Column(name = "reported_nickname", nullable = false)
    val reportedNickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportStatus = ReportStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: ReportType,

    @Column(name = "reason", columnDefinition = "TEXT")
    val reason: String? = null,
) : BaseEntity() {

    fun updateStatus(reportStatus: ReportStatus) {
        this.status = reportStatus
    }
}