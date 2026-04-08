package com.pidulgi.server.member.entity.type

import com.pidulgi.server.common.exception.CustomException

enum class Gender {

    MALE, FEMALE;

    companion object {
        fun from(value: String): Gender {
            return try {
                valueOf(value.uppercase())
            } catch (_: IllegalArgumentException) {
                throw CustomException("성별이 유효하지 않습니다. (입력: $value)")
            }
        }
    }
}