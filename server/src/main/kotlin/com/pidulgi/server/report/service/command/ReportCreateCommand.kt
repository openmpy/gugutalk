package com.pidulgi.server.report.service.command

data class ReportCreateCommand(

    val reporterId: Long,
    val reportedId: Long,
) {

    init {
        require(reporterId != reportedId) { "자기 자신을 신고할 수 없습니다." }
    }
}
