package com.pidulgi.server.member.entity.type

import com.pidulgi.server.common.exception.CustomException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class GenderTest : ShouldSpec({

    should("회원 성별이 유효하면 정상적으로 생성된다.") {
        listOf("MALE", "male").forEach { input ->
            Gender.from(input) shouldBe Gender.MALE
        }
        listOf("FEMALE", "female").forEach { input ->
            Gender.from(input) shouldBe Gender.FEMALE
        }
    }

    should("회원 성별이 유효하지 않으면 예외가 발생한다.") {
        listOf("남자", "여자").forEach { input ->
            shouldThrow<CustomException> {
                Gender.from(input)
            }
        }
    }
})