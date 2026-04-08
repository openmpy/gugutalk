package com.pidulgi.server.member.entity.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MemberBioTest : ShouldSpec({

    should("자기소개가 유효하면 정상적으로 생성된다.") {
        MemberBio("반갑습니다.").value shouldBe "반갑습니다."
    }

    should("자기소개가 1000자 초과이면 예외가 발생한다.") {
        shouldThrow<IllegalArgumentException> {
            MemberBio("가".repeat(1001))
        }.message shouldBe "자기소개는 최대 1000자 이하여야 합니다."
    }
})