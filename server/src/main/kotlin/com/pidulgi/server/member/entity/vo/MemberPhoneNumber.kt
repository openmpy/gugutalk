package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable

@Embeddable
class MemberPhoneNumber(
    value: String,
) {

    companion object {
        private val PHONE_REGEX = Regex("^010\\d{8}$")
    }

    val value: String

    init {
        validatePhoneNumber(value)
        this.value = value
    }

    private fun validatePhoneNumber(value: String) {
        require(value.isNotBlank()) {
            "휴대폰 번호는 빈 값일 수 없습니다."
        }
        require(PHONE_REGEX.matches(value)) {
            "휴대폰 번호 형식이 올바르지 않습니다."
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberPhoneNumber
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}