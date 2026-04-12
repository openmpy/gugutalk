package com.pidulgi.server.auth.entity

import com.pidulgi.server.common.jpa.BaseEntity
import com.pidulgi.server.member.entity.vo.MemberPhoneNumber
import jakarta.persistence.*

@Entity
@Table(name = "phone_verification")
class PhoneVerification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "phone_number", nullable = false)
    )
    val phoneNumber: MemberPhoneNumber,

    @Column(name = "verification_code", nullable = false)
    val verificationCode: String,

    @Column(name = "client_ip", nullable = false)
    val clientIp: String,
) : BaseEntity()