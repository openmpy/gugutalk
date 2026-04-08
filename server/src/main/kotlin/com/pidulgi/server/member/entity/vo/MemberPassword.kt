package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable

@Embeddable
class MemberPassword(

    value: String,
) {

    val value: String

    init {
        validatePassword(value)
        this.value = value
    }

    private fun validatePassword(value: String) {
        require(value.isNotBlank()) {
            "비밀번호는 빈 값일 수 없습니다."
        }
    }
}