package com.pidulgi.server.report.entity.type

import com.pidulgi.server.common.exception.CustomException

enum class ReportStatus {

    PENDING,
    RESOLVE,
    REJECT;

    companion object {
        fun from(value: String): ReportStatus {
            return try {
                ReportStatus.valueOf(value.uppercase())
            } catch (_: IllegalArgumentException) {
                throw CustomException("신고 유형이 유효하지 않습니다. (입력: $value)")
            }
        }
    }
}