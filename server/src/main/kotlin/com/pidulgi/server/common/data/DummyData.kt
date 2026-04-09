package com.pidulgi.server.common.data

import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.vo.*
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.point.entity.Point
import com.pidulgi.server.point.repository.PointRepository
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*

@ConditionalOnProperty(name = ["app.dummy-data.init"], havingValue = "true")
@Component
class DummyDataInit {

    private companion object {
        const val DUMMY_MEMBER_COUNT = 10000
    }

    private val log = KotlinLogging.logger {}
    private val geometryFactory = GeometryFactory()
    private val locations = listOf(
        Pair(126.9780, 37.5665),   // 서울 시청 (기준)
        Pair(126.9850, 37.5700),   // 약 0.8km
        Pair(126.9900, 37.5750),   // 약 1.5km
        Pair(127.0000, 37.5800),   // 약 2.5km
        Pair(127.0100, 37.5600),   // 약 3.2km
        Pair(126.9600, 37.5500),   // 약 3.8km
        Pair(127.0200, 37.5400),   // 약 5.1km
        Pair(126.9400, 37.5300),   // 약 6.5km
        Pair(127.0400, 37.5200),   // 약 8.3km
        Pair(126.9200, 37.5100),   // 약 9.7km
        Pair(127.0600, 37.5000),   // 약 11.2km
        Pair(126.9000, 37.4900),   // 약 13.0km
        Pair(127.0800, 37.4800),   // 약 14.8km
        Pair(126.8800, 37.4700),   // 약 16.5km
        Pair(127.1000, 37.4600),   // 약 18.3km
        Pair(126.8600, 37.4500),   // 약 20.1km
        Pair(127.1200, 37.4400),   // 약 22.0km
        Pair(126.8400, 37.4300),   // 약 24.2km
        Pair(127.1400, 37.4200),   // 약 26.1km
        Pair(126.8200, 37.4100),   // 약 28.0km
    )

    @Bean
    fun init(
        memberRepository: MemberRepository,
        pointRepository: PointRepository,
        likeRepository: LikeRepository,
        privateImageGrantRepository: PrivateImageGrantRepository,
        blockRepository: BlockRepository,
        chatRoomRepository: ChatRoomRepository,
        messageRepository: MessageRepository,
    ): CommandLineRunner {
        return CommandLineRunner {
            // 회원
            if (memberRepository.count() == 0L) {
                val members = (0 until DUMMY_MEMBER_COUNT).map { i ->
                    val location = locations[i % locations.size].let { (lng, lat) ->
                        val offsetLng = lng + (i / locations.size) * 0.002
                        val offsetLat = lat + (i / locations.size) * 0.001

                        geometryFactory.createPoint(Coordinate(offsetLng, offsetLat)).also {
                            it.srid = 4326
                        }
                    }

                    Member(
                        uuid = MemberUuid(UUID.randomUUID().toString()),
                        phoneNumber = MemberPhoneNumber("0100000%04d".format(i)),
                        password = MemberPassword("1234"),
                        nickname = MemberNickname("닉네임$i"),
                        gender = if (i % 3 == 0) Gender.MALE else Gender.FEMALE,
                        birthYear = MemberBirthYear(1993 + (i % 12)),
                        bio = MemberBio("자기소개$i"),
                        comment = MemberComment("코멘트$i"),
                        location = location,
                    )
                }
                memberRepository.saveAll(members)
            }

            // 포인트
            if (pointRepository.count() == 0L) {
                val points = (1 until DUMMY_MEMBER_COUNT + 1).map { i ->
                    Point(memberId = i.toLong())
                }
                pointRepository.saveAll(points)
            }

            // 좋아요
            if (likeRepository.count() == 0L) {
                val likes = (2 until DUMMY_MEMBER_COUNT + 1).map { i ->
                    Like(
                        likerId = 1,
                        likedId = i.toLong()
                    )
                }
                likeRepository.saveAll(likes)
            }

            // 비밀 사진 권한
            if (privateImageGrantRepository.count() == 0L) {
                val privateImageGrants = (2 until DUMMY_MEMBER_COUNT + 1).map { i ->
                    PrivateImageGrant(
                        granterId = 1,
                        granteeId = i.toLong()
                    )
                }
                privateImageGrantRepository.saveAll(privateImageGrants)
            }

            // 차단
            if (blockRepository.count() == 0L) {
                val blocks = (2 until DUMMY_MEMBER_COUNT + 1).map { i ->
                    Block(
                        blockerId = 1,
                        blockedId = i.toLong()
                    )
                }
                blockRepository.saveAll(blocks)
            }

            // 로그
            log.info { "회원 더미 데이터 (${memberRepository.count()})개" }
            log.info { "포인트 더미 데이터 (${pointRepository.count()})개" }
            log.info { "좋아요 더미 데이터 (${likeRepository.count()})개" }
            log.info { "비밀 사진 권한 더미 데이터 (${privateImageGrantRepository.count()})개" }
            log.info { "차단 더미 데이터 (${blockRepository.count()})개" }
        }
    }
}