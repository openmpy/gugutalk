package com.pidulgi.server.member.controller

import com.pidulgi.server.common.auth.Login
import com.pidulgi.server.common.s3.dto.response.PresignedUrlsResponse
import com.pidulgi.server.member.dto.request.MemberGetPresignedUrlsRequest
import com.pidulgi.server.member.dto.response.MemberGetPrivateImagesResponse
import com.pidulgi.server.member.service.MemberImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class MemberImageController(

    private val memberImageService: MemberImageService,
) {

    @PostMapping("/v1/members/images/presigned")
    fun getPresignedUrls(
        @Login memberId: Long,
        @RequestBody request: MemberGetPresignedUrlsRequest
    ): ResponseEntity<PresignedUrlsResponse> {
        val response = memberImageService.getPresignedUrls(memberId, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/members/private-images/{granterId}")
    fun getPrivateImages(
        @Login granteeId: Long,
        @PathVariable granterId: Long
    ): ResponseEntity<MemberGetPrivateImagesResponse> {
        val response = memberImageService.getPrivateImages(granteeId, granterId)
        return ResponseEntity.ok(response)
    }
}