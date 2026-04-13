package com.pidulgi.server.admin.controller

import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.admin.service.AdminService
import com.pidulgi.server.admin.service.query.AdminGetMembersQuery
import com.pidulgi.server.admin.service.query.AdminGetReportsQuery
import com.pidulgi.server.common.dto.CursorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RequestMapping("/api")
@RestController
class AdminController(

    private val adminService: AdminService,
) {

    @GetMapping("/v1/admin/members")
    fun getMembers(
        @RequestParam(defaultValue = "NICKNAME") type: String,
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "ALL") gender: String,
        @RequestParam(required = false) cursorId: Long? = null,
        @RequestParam(required = false) cursorDate: LocalDateTime? = null,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<AdminGetMemberResponse>> {
        val query = AdminGetMembersQuery(type, keyword, gender, cursorId, cursorDate, size)
        val response = adminService.getMembers(query)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/reports")
    fun getReports(
        @RequestParam(defaultValue = "NICKNAME") type: String,
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "PENDING") status: String,
        @RequestParam(required = false) cursorId: Long? = null,
        @RequestParam(required = false) cursorDate: LocalDateTime? = null,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<CursorResponse<AdminGetReportResponse>> {
        val query = AdminGetReportsQuery(type, keyword, status, cursorId, cursorDate, size)
        val response = adminService.getReports(query)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/members/{memberId}")
    fun getMember(
        @PathVariable memberId: Long,
    ): ResponseEntity<AdminGetMemberDetailResponse> {
        val response = adminService.getMember(memberId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/v1/admin/members/{memberId}/nickname")
    fun sanitizeNickname(
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        adminService.sanitizeNickname(memberId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/admin/members/{memberId}/comment")
    fun sanitizeComment(
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        adminService.sanitizeComment(memberId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/admin/members/{memberId}/bio")
    fun sanitizeBio(
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        adminService.sanitizeBio(memberId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/admin/members/{memberId}/images/{imageId}")
    fun deleteMemberImage(
        @PathVariable memberId: Long,
        @PathVariable imageId: Long,
    ): ResponseEntity<Unit> {
        adminService.deleteMemberImage(memberId, imageId)
        return ResponseEntity.ok().build()
    }
}