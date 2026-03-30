package com.pidulgi.server.auth.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "phone_verification")
class PhoneVerification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,

    @Column(name = "verification_code", nullable = false)
    val verificationCode: String,

    @Column(name = "client_ip", nullable = false)
    val clientIp: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)