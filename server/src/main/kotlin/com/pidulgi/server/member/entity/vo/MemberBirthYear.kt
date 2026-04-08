package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable
import org.threeten.bp.LocalDateTime

@Embeddable
class MemberBirthYear(
    value: Int,
) {

    companion object {
        private const val MIN_AGE = 20
        private const val MAX_AGE = 60
    }

    val value: Int

    init {
        validateBirthYear(value)
        this.value = value
    }

    private fun validateBirthYear(value: Int) {
        val age = LocalDateTime.now().year - value

        require(age >= MIN_AGE) {
            "나이는 최소 ${MIN_AGE}세 이상이어야 합니다."
        }
        require(age <= MAX_AGE) {
            "나이는 최대 ${MAX_AGE}세 이하여야 합니다."
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberBirthYear
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}