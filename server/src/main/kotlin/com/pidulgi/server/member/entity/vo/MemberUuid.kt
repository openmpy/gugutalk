package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable

@Embeddable
class MemberUuid(
    value: String,
) {

    val value: String

    init {
        validateUuid(value)
        this.value = value
    }

    private fun validateUuid(value: String) {
        require(value.isNotBlank()) {
            "UUID는 빈 값일 수 없습니다."
        }
    }
}