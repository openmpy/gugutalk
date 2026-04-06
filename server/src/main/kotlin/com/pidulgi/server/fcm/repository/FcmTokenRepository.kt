package com.pidulgi.server.fcm.repository

import com.pidulgi.server.fcm.entity.FcmToken
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

    fun findByToken(token: String): FcmToken?

    fun findByMemberIdAndUuid(memberId: Long, uuid: String): FcmToken?
}