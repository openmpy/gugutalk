package com.pidulgi.server.member.entity.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MemberCommentTest : ShouldSpec({

    should("코멘트가 유효하면 정상적으로 생성된다.") {
        MemberComment("반갑습니다.").value shouldBe "반갑습니다."
    }

    should("코멘트가 100자 초과이면 예외가 발생한다.") {
        shouldThrow<IllegalArgumentException> {
            MemberComment("가".repeat(101))
        }.message shouldBe "코멘트는 최대 100자 이하여야 합니다."
    }
})