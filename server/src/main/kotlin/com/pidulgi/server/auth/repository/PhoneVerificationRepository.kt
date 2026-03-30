package com.pidulgi.server.auth.repository

import com.pidulgi.server.auth.entity.PhoneVerification
import org.springframework.data.jpa.repository.JpaRepository

interface PhoneVerificationRepository : JpaRepository<PhoneVerification, Long>