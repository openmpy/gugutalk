package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.MemberImage
import org.springframework.data.jpa.repository.JpaRepository

interface MemberImageRepository : JpaRepository<MemberImage, Long>