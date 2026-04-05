package com.pidulgi.server.member.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class MemberSchedulerIntegrationTest {

    @Autowired
    lateinit var memberScheduler: MemberScheduler

    @Test
    fun `cleanUpMembers 실행`() {
        memberScheduler.cleanUpMembers()
    }
}