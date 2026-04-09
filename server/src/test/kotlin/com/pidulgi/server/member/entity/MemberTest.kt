package com.pidulgi.server.member.entity

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.type.MemberRole
import com.pidulgi.server.member.entity.vo.MemberPassword
import com.pidulgi.server.member.entity.vo.MemberPhoneNumber
import com.pidulgi.server.member.entity.vo.MemberUuid
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MemberTest : ShouldSpec({

    fun createMember(): Member {
        return Member(
            uuid = MemberUuid("uuid"),
            phoneNumber = MemberPhoneNumber("01012345678"),
            password = MemberPassword("password"),
            gender = Gender.MALE,
        )
    }

    should("회원이 유효하면 정상적으로 생성된다.") {
        val member = createMember()

        member.uuid.value shouldBe "uuid"
        member.phoneNumber.value shouldBe "01012345678"
        member.password.value shouldBe "password"
        member.gender shouldBe Gender.MALE
        member.isChatEnabled shouldBe true
        member.role shouldBe MemberRole.MEMBER
        member.deletedAt shouldBe null
    }

    should("계정을 활성화한다.") {
        val member = createMember()

        member.activate(
            profileKey = "key",
            nickname = "홍길동",
            birthYear = 2006,
            bio = "안녕하세요"
        )

        member.profileKey shouldBe "key"
        member.nickname.value shouldBe "홍길동"
        member.birthYear.value shouldBe 2006
        member.bio?.value shouldBe "안녕하세요"
    }

    should("회원 탈퇴 시 deletedAt이 설정된다.") {
        val member = createMember()

        member.withdraw()

        member.deletedAt shouldNotBe null
    }

    should("위치를 업데이트하면 updatedAt도 변경된다.") {
        val member = createMember()
        val before = member.updatedAt

        member.bump(null)

        member.updatedAt.isAfter(before) shouldBe true
    }

    should("닉네임을 수정한다.") {
        val member = createMember()

        member.updateNickname("새닉네임")

        member.nickname.value shouldBe "새닉네임"
    }

    should("자기소개를 수정한다.") {
        val member = createMember()

        member.updateBio("자기소개")

        member.bio?.value shouldBe "자기소개"
    }

    should("코멘트를 수정한다.") {
        val member = createMember()

        member.updateComment("코멘트")

        member.comment.value shouldBe "코멘트"
    }

    should("프로필 전체를 수정한다.") {
        val member = createMember()

        member.updateProfile(
            profileKey = "newKey",
            nickname = "닉네임",
            birthYear = 1999,
            bio = "소개"
        )

        member.profileKey shouldBe "newKey"
        member.nickname.value shouldBe "닉네임"
        member.birthYear.value shouldBe 1999
        member.bio?.value shouldBe "소개"
    }

    should("프로필 키만 수정한다.") {
        val member = createMember()

        member.updateProfileKey("onlyKey")

        member.profileKey shouldBe "onlyKey"
    }

    should("채팅 활성화 상태를 토글한다.") {
        val member = createMember()

        val before = member.isChatEnabled
        member.toggleChatEnabled()

        member.isChatEnabled shouldBe !before
    }
})