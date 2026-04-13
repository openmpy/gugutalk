package com.pidulgi.server.admin.controller

import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.admin.service.AdminService
import com.pidulgi.server.admin.service.query.AdminGetMembersQuery
import com.pidulgi.server.admin.service.query.AdminGetReportsQuery
import com.pidulgi.server.common.dto.CursorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
}