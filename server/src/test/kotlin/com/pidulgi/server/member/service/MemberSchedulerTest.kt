package com.pidulgi.server.member.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MemberSchedulerTest {

    @Autowired
    private lateinit var scheduler: MemberScheduler

    @Test
    fun `cleanUpMembers 실행`() {
        scheduler.cleanUpMembers()
    }
}