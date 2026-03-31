package com.pidulgi.server.common.data

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.LikeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*

@Component
class DummyDataInit {

    @Bean
    fun init(
        memberRepository: MemberRepository,
        likeRepository: LikeRepository,
    ): CommandLineRunner {
        return CommandLineRunner {
            if (memberRepository.count() == 0L) {
                val members = (1 until 100).map { i ->
                    Member(
                        uuid = UUID.randomUUID().toString(),
                        phoneNumber = "01000000%03d".format(i),
                        password = "1234",
                        nickname = "닉네임$i",
                        gender = if (i % 2 == 0) Gender.MALE else Gender.FEMALE,
                    )
                }

                memberRepository.saveAll(members)
                println("회원 데이터가 생성되었습니다. ${memberRepository.count()}")
            }

            if (likeRepository.count() == 0L) {
                val likes = (2 until 100).map { i ->
                    Like(
                        likerId = 1,
                        likedId = i.toLong()
                    )
                }

                likeRepository.saveAll(likes)
                println("좋아요 데이터가 생성되었습니다. ${likeRepository.count()}")
            }
        }
    }
}