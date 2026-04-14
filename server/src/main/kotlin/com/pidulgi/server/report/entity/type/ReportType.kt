package com.pidulgi.server.report.entity.type

import com.pidulgi.server.common.exception.CustomException

enum class ReportType(val text: String) {

    ABUSE("욕설 / 비방"),
    SPAM("스팸 / 광고"),
    MINOR("미성년자"),
    SEXUAL("음란물"),
    FAKE("도용"),
    ETC("기타");

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