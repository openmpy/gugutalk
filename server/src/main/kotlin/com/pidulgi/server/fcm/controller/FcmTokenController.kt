package com.pidulgi.server.fcm.controller

import com.pidulgi.server.fcm.dto.request.FcmTokenRegisterRequest
import com.pidulgi.server.fcm.service.FcmTokenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class FcmTokenController(

    private val fcmTokenService: FcmTokenService
) {

    @PostMapping("/v1/fcm/token")
    fun register(
        @RequestBody request: FcmTokenRegisterRequest
    ): ResponseEntity<Unit> {
        fcmTokenService.register(request)
        return ResponseEntity.ok().build()
    }
}