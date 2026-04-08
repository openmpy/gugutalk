package com.pidulgi.server.member.entity.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MemberPhoneNumberTest : ShouldSpec({

    should("휴대폰 번호가 유효하면 정상적으로 생성된다.") {
        MemberPhoneNumber("01012345678").value shouldBe "01012345678"
    }

    should("휴대폰 번호가 같으면 동등하다.") {
        MemberPhoneNumber("01012345678") shouldBe MemberPhoneNumber("01012345678")
    }

    should("휴대폰 번호가 다르면 동등하지 않다.") {
        MemberPhoneNumber("01012345678") shouldNotBe MemberPhoneNumber("01087654321")
    }

    should("휴대폰 번호가 빈 값이면 예외가 발생한다.") {
        listOf("", " ").forEach { input ->
            shouldThrow<IllegalArgumentException> {
                MemberPhoneNumber(input)
            }.message shouldBe "휴대폰 번호는 빈 값일 수 없습니다."
        }
    }

    should("휴대폰 번호가 잘못된 형식이면 예외가 발생한다.") {
        listOf(
            "0101234567",
            "010123456789",
            "01112345678",
            "0101234567a",
            "010-1234-5678"
        ).forEach { input ->
            shouldThrow<IllegalArgumentException> {
                MemberPhoneNumber(input)
            }.message shouldBe "휴대폰 번호 형식이 올바르지 않습니다."
        }
    }
})