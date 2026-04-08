package com.pidulgi.server.member.entity.vo

import jakarta.persistence.Embeddable

@Embeddable
class MemberComment(
    value: String,
) {

    companion object {
        private const val COMMENT_MAX_LENGTH = 100
    }

    val value: String

    init {
        validateComment(value)
        this.value = value
    }

    private fun validateComment(value: String) {
        require(value.length <= COMMENT_MAX_LENGTH) {
            "코멘트는 최대 ${COMMENT_MAX_LENGTH}자 이하여야 합니다."
        }
    }
}