package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable

@Embeddable
class MemberNickname(
    value: String,
) {

    companion object {
        private const val NICKNAME_MIN_LENGTH = 2
        private const val NICKNAME_MAX_LENGTH = 10
    }

    val value: String

    init {
        validateNickname(value)
        this.value = value
    }

    private fun validateNickname(value: String) {
        require(value.isNotBlank()) {
            "닉네임이 빈 값일 수 없습니다."
        }
        require(value == value.trim()) {
            "닉네임 앞뒤에 공백을 포함할 수 없습니다."
        }
        require(value.length >= NICKNAME_MIN_LENGTH) {
            "닉네임은 최소 ${NICKNAME_MIN_LENGTH}자 이상이어야 합니다."
        }
        require(value.length <= NICKNAME_MAX_LENGTH) {
            "닉네임은 최대 ${NICKNAME_MAX_LENGTH}자 이하이어야 합니다."
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberNickname
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}