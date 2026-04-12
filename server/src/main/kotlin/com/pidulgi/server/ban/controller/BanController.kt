package com.pidulgi.server.ban.controller

import com.pidulgi.server.ban.dto.request.BanAddRequest
import com.pidulgi.server.ban.service.BanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class BanController(

    private val banService: BanService
) {

    @PostMapping("/v1/admin/bans")
    fun add(@RequestBody request: BanAddRequest): ResponseEntity<Unit> {
        banService.add(request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/admin/bans/{banId}")
    fun remove(@PathVariable banId: Long): ResponseEntity<Unit> {
        banService.remove(banId)
        return ResponseEntity.ok().build()
    }
}