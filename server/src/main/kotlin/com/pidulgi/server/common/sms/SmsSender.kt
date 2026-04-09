package com.pidulgi.server.common.sms

import com.pidulgi.server.common.exception.CustomException
import com.solapi.sdk.SolapiClient
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException
import com.solapi.sdk.message.exception.SolapiUnknownException
import com.solapi.sdk.message.model.Message
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SmsSender(
    @Value("\${sms.api-key}") private val apiKey: String,
    @Value("\${sms.secret-key}") private val secretKey: String,
    @Value("\${sms.phone}") private val phone: String,
) {

    private val log = KotlinLogging.logger {}

    private val messageService by lazy {
        SolapiClient.createInstance(apiKey, secretKey)
    }

    fun send(to: String, text: String) {
        val message = Message().apply {
            this.from = phone
            this.to = to
            this.text = text
        }

        try {
            messageService.send(message)
        } catch (e: Exception) {
            when (e) {
                is SolapiMessageNotReceivedException, is SolapiUnknownException -> {
                    log.error(e) { e.message }
                    throw CustomException("문자 전송에 실패했습니다.")
                }

                else -> throw RuntimeException(e)
            }
        }
    }
}