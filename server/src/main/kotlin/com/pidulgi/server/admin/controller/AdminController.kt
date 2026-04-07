package com.pidulgi.server.admin.controller

import com.pidulgi.server.admin.dto.response.AdminGetMemberResponse
import com.pidulgi.server.admin.service.AdminService
import com.pidulgi.server.common.dto.PageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
}