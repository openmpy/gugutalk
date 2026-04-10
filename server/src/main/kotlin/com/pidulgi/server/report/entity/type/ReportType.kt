package com.pidulgi.server.report.entity.type

import com.pidulgi.server.common.exception.CustomException

enum class ReportType {

    ABUSE,
    SPAM,
    MINOR,
    SEXUAL,
    FAKE,
    ETC;

    companion object {
        fun from(value: String): ReportType {
            return try {
                ReportType.valueOf(value.uppercase())
            } catch (_: IllegalArgumentException) {
                throw CustomException("신고 유형이 유효하지 않습니다. (입력: $value)")
            }
        }
    }
}