package com.pidulgi.server.member.entity.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MemberNicknameTest : ShouldSpec({

    should("닉네임이 유효하면 정상적으로 생성된다.") {
        MemberNickname("홍길동").value shouldBe "홍길동"
    }

    should("닉네임이 같으면 동등하다.") {
        MemberNickname("홍길동") shouldBe MemberNickname("홍길동")
    }

    should("닉네임이 다르면 동등하지 않다.") {
        MemberNickname("홍길동") shouldNotBe MemberNickname("박명수")
    }

    should("닉네임이 빈 값이면 예외가 발생한다.") {
        listOf("", " ").forEach { input ->
            shouldThrow<IllegalArgumentException> {
                MemberNickname(input)
            }.message shouldBe "닉네임이 빈 값일 수 없습니다."
        }
    }

    should("닉네임 앞뒤에 공백이 포함되어 있으면 예외가 발생한다.") {
        listOf(" 홍길동", " 홍길동 ", "홍길동 ").forEach { input ->
            shouldThrow<IllegalArgumentException> {
                MemberNickname(input)
            }.message shouldBe "닉네임 앞뒤에 공백을 포함할 수 없습니다."
        }
    }

    should("닉네임 길이가 2자 미만일 경우 예외가 발생한다.") {
        shouldThrow<IllegalArgumentException> {
            MemberNickname("홍")
        }
    }

    should("닉네임 길이가 10자 초과일 경우 예외가 발생한다.") {
        shouldThrow<IllegalArgumentException> {
            MemberNickname("홍".repeat(11))
        }
    }
})