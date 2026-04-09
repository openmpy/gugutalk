package com.pidulgi.server.report.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ReportCleanUpSchedulerTest {

    @Autowired
    private lateinit var scheduler: ReportScheduler

    @Test
    fun `cleanUpReports 실행`() {
        scheduler.cleanUpReports()
    }
}