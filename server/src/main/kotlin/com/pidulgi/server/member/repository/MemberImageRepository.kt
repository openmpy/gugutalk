package com.pidulgi.server.member.repository

import com.pidulgi.server.member.entity.MemberImage
import com.pidulgi.server.member.entity.type.ImageStatus
import com.pidulgi.server.member.entity.type.ImageType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface MemberImageRepository : JpaRepository<MemberImage, Long> {

    fun findAllByMemberIdAndTypeOrderBySortOrder(
        memberId: Long,
        type: ImageType
    ): List<MemberImage>

    fun findAllByMemberId(memberId: Long): List<MemberImage>

    fun findAllByMemberIdIn(memberIds: Collection<Long>): List<MemberImage>

    fun findAllByKeyIn(keys: Collection<String>): List<MemberImage>

    fun deleteAllByIdIn(ids: List<Long>)

    fun findAllByStatusAndCreatedAtBefore(
        status: ImageStatus,
        createdAtBefore: LocalDateTime
    ): List<MemberImage>
}