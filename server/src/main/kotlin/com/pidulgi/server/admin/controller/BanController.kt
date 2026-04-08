package com.pidulgi.server.admin.controller

import com.pidulgi.server.admin.dto.request.BanAddRequest
import com.pidulgi.server.admin.dto.response.BanGetDetailResponse
import com.pidulgi.server.admin.dto.response.BanGetMemberResponse
import com.pidulgi.server.admin.dto.response.BanGetResponse
import com.pidulgi.server.admin.service.BanService
import com.pidulgi.server.common.dto.PageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class BanController(

    private val banService: BanService,
) {

    @PostMapping("/v1/admin/bans")
    fun add(
        @RequestBody request: BanAddRequest,
    ): ResponseEntity<Unit> {
        banService.add(request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/admin/bans/{banId}")
    fun remove(
        @PathVariable banId: Long
    ): ResponseEntity<Unit> {
        banService.remove(banId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/admin/bans")
    fun gets(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
    ): ResponseEntity<PageResponse<BanGetResponse>> {
        val response = banService.gets(page, size)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/bans/{banId}")
    fun get(
        @PathVariable banId: Long,
    ): ResponseEntity<BanGetDetailResponse> {
        val response = banService.get(banId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/bans/members")
    fun getMemberByUuid(
        @RequestParam(value = "uuid") uuid: String,
    ): ResponseEntity<BanGetMemberResponse> {
        val response = banService.getMemberByUuid(uuid)
        return ResponseEntity.ok(response)
    }
}