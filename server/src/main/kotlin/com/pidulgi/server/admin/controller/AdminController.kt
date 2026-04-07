package com.pidulgi.server.admin.controller

import com.pidulgi.server.admin.dto.request.BanAddRequest
import com.pidulgi.server.admin.dto.request.BanUpdateRequest
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminMemberResponse
import com.pidulgi.server.admin.entity.Ban
import com.pidulgi.server.admin.service.AdminService
import com.pidulgi.server.admin.service.BanService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class AdminController(

    private val adminService: AdminService,
    private val banService: BanService,
) {

    @GetMapping("/v1/admin/members")
    fun getMembers(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
    ): ResponseEntity<Page<AdminMemberResponse>> {
        val response = adminService.getMembers(page, size)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/members/{memberId}")
    fun getMember(
        @PathVariable memberId: Long,
    ): ResponseEntity<AdminGetMemberResponse> {
        val response = adminService.get(memberId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/v1/admin/bans")
    fun addBan(
        @RequestParam(value = "memberId", required = true) memberId: Long,
        @RequestBody request: BanAddRequest
    ): ResponseEntity<Unit> {
        banService.add(memberId, request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/admin/bans/{banId}")
    fun updateBan(
        @PathVariable banId: Long,
        @RequestBody request: BanUpdateRequest
    ): ResponseEntity<Unit> {
        banService.update(banId, request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/admin/bans/{banId}")
    fun removeBan(
        @PathVariable banId: Long,
    ): ResponseEntity<Unit> {
        banService.remove(banId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/admin/bans")
    fun getBans(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
    ): ResponseEntity<Page<Ban>> {
        val response = banService.gets(page, size)
        return ResponseEntity.ok(response)
    }
}