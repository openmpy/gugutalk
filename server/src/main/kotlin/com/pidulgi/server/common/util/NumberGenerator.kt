package com.pidulgi.server.common.util

import java.security.SecureRandom

object NumberGenerator {

    fun generate(): String {
        val randomNumber = 10000 + SecureRandom().nextInt(90000)
        return randomNumber.toString()
    }
}