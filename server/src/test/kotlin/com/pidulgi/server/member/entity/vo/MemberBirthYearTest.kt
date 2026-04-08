package com.pidulgi.server.member.entity.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MemberBirthYearTest : ShouldSpec({

    should("출생연도가 유효하면 정상적으로 생성된다.") {
        MemberBirthYear(2006).value shouldBe 2006
    }

    should("출생연도가 같으면 동등하다.") {
        MemberBirthYear(2006) shouldBe MemberBirthYear(2006)
    }

    should("출생연도가 다르면 동등하지 않다.") {
        MemberBirthYear(2006) shouldNotBe MemberBirthYear(2005)
    }

    should("나이가 20세 미만일 경우 예외가 발생한다.") {
        shouldThrow<IllegalArgumentException> {
            MemberBirthYear(2007).value
        }.message shouldBe "나이는 최소 20세 이상이어야 합니다."
    }

    should("나이가 60세 이상일 경우 예외가 발생한다.") {
        shouldThrow<IllegalArgumentException> {
            MemberBirthYear(1965).value
        }.message shouldBe "나이는 최대 60세 이하여야 합니다."
    }
})