package com.pidulgi.server.admin.controller

import com.pidulgi.server.admin.dto.response.AdminGetMemberDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportDetailResponse
import com.pidulgi.server.admin.dto.response.AdminGetReportResponse
import com.pidulgi.server.admin.service.AdminService
import com.pidulgi.server.common.dto.PageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class AdminController(

    private val adminService: AdminService
) {

    @GetMapping("/v1/admin/members")
    fun getMembers(
        @RequestParam(value = "gender", defaultValue = "ALL") gender: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
    ): ResponseEntity<PageResponse<AdminGetMemberResponse>> {
        val response = adminService.getMembers(gender, page, size)
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
    fun updateMemberNickname(
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        adminService.updateMemberNickname(memberId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/admin/members/{memberId}/comment")
    fun updateMemberComment(
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        adminService.updateMemberComment(memberId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/admin/members/{memberId}/bio")
    fun updateMemberBio(
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        adminService.updateMemberBio(memberId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/admin/members/search")
    fun searchMembers(
        @RequestParam(value = "keyword", required = true) keyword: String,
        @RequestParam(value = "gender", defaultValue = "ALL") gender: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
    ): ResponseEntity<PageResponse<AdminGetMemberResponse>> {
        val response = adminService.searchMembers(keyword, gender, page, size)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/admin/members/{memberId}/images/{imageId}")
    fun deleteMemberImage(
        @PathVariable memberId: Long,
        @PathVariable imageId: Long,
    ): ResponseEntity<Unit> {
        adminService.deleteMemberImage(memberId, imageId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/admin/reports")
    fun getReports(
        @RequestParam(value = "status", defaultValue = "PENDING") status: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
    ): ResponseEntity<PageResponse<AdminGetReportResponse>> {
        val response = adminService.getReports(status, page, size)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/admin/reports/{reportId}")
    fun getReport(
        @PathVariable reportId: Long,
    ): ResponseEntity<AdminGetReportDetailResponse> {
        val response = adminService.getReport(reportId)
        return ResponseEntity.ok(response)
    }
}