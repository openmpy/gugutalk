package com.pidulgi.server.common.util

import java.time.LocalDateTime

object AgeCalculator {

    fun calculate(birthYear: Int): Int {
        return LocalDateTime.now().year - birthYear
    }
}