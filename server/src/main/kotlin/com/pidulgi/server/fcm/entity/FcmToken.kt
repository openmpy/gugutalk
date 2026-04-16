package com.pidulgi.server.fcm.entity

import com.pidulgi.server.common.jpa.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "fcm_token")
class FcmToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "token", unique = true, nullable = false)
    var token: String,

    @Column(name = "uuid", nullable = false)
    var uuid: String,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity() {

    fun update(uuid: String, memberId: Long?) {
        this.uuid = uuid
        this.memberId = memberId
        this.updatedAt = LocalDateTime.now()
    }
}