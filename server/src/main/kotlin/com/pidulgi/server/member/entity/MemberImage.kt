package com.pidulgi.server.member.entity

import com.pidulgi.server.member.entity.type.ImageStatus
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
    var memberId: Long = 0,

    @Column(name = "key", nullable = false)
    val key: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ImageType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ImageStatus = ImageStatus.PENDING,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {

    fun upload(memberId: Long, sortOrder: Int) {
        this.memberId = memberId
        this.status = ImageStatus.COMPLETE
        this.sortOrder = sortOrder
    }

    fun updateSortOrder(index: Int) {
        this.sortOrder = index
    }
}