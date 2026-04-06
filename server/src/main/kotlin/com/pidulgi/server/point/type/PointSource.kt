package com.pidulgi.server.point.type

enum class PointSource(val point: Long, val description: String) {

    // EARN
    ATTENDANCE(30, "출석 체크"),
    AD_REWARD(15, "광고 보상"),

    // USE
    SEND_MESSAGE(15, "쪽지 전송"),
}