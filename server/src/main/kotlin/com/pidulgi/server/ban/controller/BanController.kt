package com.pidulgi.server.ban.controller

import com.pidulgi.server.ban.dto.request.BanAddRequest
import com.pidulgi.server.ban.dto.response.BanGetDetailResponse
import com.pidulgi.server.ban.dto.response.BanGetResponse
import com.pidulgi.server.ban.service.BanService
import com.pidulgi.server.ban.service.query.BanGetsQuery
import com.pidulgi.server.common.dto.CursorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

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

    @GetMapping("/v1/admin/bans")
    fun gets(
        @RequestParam(defaultValue = "UUID") type: String,
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(required = false) cursorId: Long? = null,
        @RequestParam(required = false) cursorDate: LocalDateTime? = null,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<BanGetResponse>> {
        val query = BanGetsQuery(type, keyword, cursorId, cursorDate, size)
        val response = banService.gets(query)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/bans/{banId}")
    fun get(@PathVariable banId: Long): ResponseEntity<BanGetDetailResponse> {
        val response = banService.get(banId)
        return ResponseEntity.ok(response)
    }
}