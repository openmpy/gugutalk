package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.ImageType
import org.springframework.data.jpa.repository.JpaRepository

interface MemberImageRepository : JpaRepository<MemberImage, Long> {

    fun findByMemberIdAndTypeOrderBySortOrder(
        memberId: Long,
        type: ImageType
    ): List<MemberImage>
}