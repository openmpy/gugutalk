package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>, MemberCustomRepository {

    fun existsByPhoneNumber(phoneNumber: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun findByPhoneNumber(phoneNumber: String): Member?


}