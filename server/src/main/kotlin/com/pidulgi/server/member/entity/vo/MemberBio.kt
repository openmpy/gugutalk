package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable

@Embeddable
class MemberBio(
    value: String,
) {

    companion object {
        private const val BIO_MAX_LENGTH = 1000
    }

    val value: String

    init {
        validateBio(value)
        this.value = value
    }

    private fun validateBio(value: String) {
        require(value.length <= BIO_MAX_LENGTH) {
            "자기소개는 최대 ${BIO_MAX_LENGTH}자 이하여야 합니다."
        }
    }
}