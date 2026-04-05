package com.pidulgi.server.common.data

import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.entity.type.MessageType
import com.pidulgi.server.chat.repository.ChatRoomRepository
import com.pidulgi.server.chat.repository.MessageRepository
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@ConditionalOnProperty(name = ["app.dummy-data.init"], havingValue = "true")
@Component
class DummyDataInit {

    private companion object {
        const val DUMMY_MEMBER_COUNT = 100
        const val DUMMY_MESSAGES_PER_ROOM = 100
    }

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
        likeRepository: LikeRepository,
        privateImageGrantRepository: PrivateImageGrantRepository,
        blockRepository: BlockRepository,
        chatRoomRepository: ChatRoomRepository,
        messageRepository: MessageRepository,
    ): CommandLineRunner {
        return CommandLineRunner {
            // 회원
            if (memberRepository.count() == 0L) {
                val members = (1 until DUMMY_MEMBER_COUNT + 1).map { i ->
                    val location = locations[(i - 1) % locations.size].let { (lng, lat) ->
                        val offsetLng = lng + (i / locations.size) * 0.002
                        val offsetLat = lat + (i / locations.size) * 0.001

                        geometryFactory.createPoint(Coordinate(offsetLng, offsetLat)).also {
                            it.srid = 4326
                        }
                    }
                    Member(
                        uuid = UUID.randomUUID().toString(),
                        phoneNumber = "0100000%04d".format(i),
                        password = "1234",
                        nickname = "닉네임$i",
                        gender = if (i % 2 == 0) Gender.MALE else Gender.FEMALE,
                        birthYear = 1993 + (i % 12),
                        bio = "자기소개$i",
                        comment = "코멘트$i",
                    ).also { it.bump(location) }
                }
                memberRepository.saveAll(members)
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

            // 비밀 사진
            if (privateImageGrantRepository.count() == 0L) {
                val grants = (2 until DUMMY_MEMBER_COUNT + 1).map { i ->
                    PrivateImageGrant(
                        granterId = 1,
                        granteeId = i.toLong()
                    )
                }

                privateImageGrantRepository.saveAll(grants)
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

            // 채팅방 + 메시지
            if (chatRoomRepository.count() == 0L) {
                for (otherId in 2 until DUMMY_MEMBER_COUNT + 1) {
                    val room = chatRoomRepository.save(ChatRoom.of(1L, otherId.toLong()))
                    val start = LocalDateTime.now().minusDays(otherId.toLong()).minusHours(1)
                    var lastContent = ""
                    var lastAt = start

                    repeat(DUMMY_MESSAGES_PER_ROOM) { idx ->
                        val sender = if (idx % 2 == 0) 1L else otherId.toLong()
                        val at = start.plusMinutes((idx * 7).toLong())
                        val content = "더미 메시지 #$otherId (${idx + 1}/$DUMMY_MESSAGES_PER_ROOM)"
                        val message = Message(
                            chatRoom = room,
                            senderId = sender,
                            content = content,
                            type = MessageType.TEXT,
                            createdAt = at,
                        )
                        messageRepository.save(message)
                        lastContent = content
                        lastAt = at
                    }
                    room.update(lastContent, lastAt)
                    chatRoomRepository.save(room)
                }
            }

            println("회원 데이터가 생성되었습니다. ${memberRepository.count()}")
            println("좋아요 데이터가 생성되었습니다. ${likeRepository.count()}")
            println("비밀 사진 권한 데이터가 생성되었습니다. ${privateImageGrantRepository.count()}")
            println("차단 데이터가 생성되었습니다. ${blockRepository.count()}")
            println("채팅방 데이터가 생성되었습니다. ${chatRoomRepository.count()}")
            println("메시지 데이터가 생성되었습니다. ${messageRepository.count()}")
        }
    }
}