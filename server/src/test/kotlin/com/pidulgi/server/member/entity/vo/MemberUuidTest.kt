package com.pidulgi.server.member.entity.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MemberUuidTest : ShouldSpec({

    should("UUID가 유효하면 정상적으로 생성된다.") {
        MemberUuid("1234").value shouldBe "1234"
    }

    should("UUID가 빈 값이면 예외가 발생한다.") {
        listOf("", " ").forEach { input ->
            shouldThrow<IllegalArgumentException> {
                MemberUuid(input)
            }.message shouldBe "UUID는 빈 값일 수 없습니다."
        }
    }
})