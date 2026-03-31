package com.pidulgi.server.common.s3

import com.pidulgi.server.common.s3.PresignedUrlsResponse.PresignedUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class S3Service(

    private val s3Presigner: S3Presigner,
) {

    @Value("\${s3.bucket}")
    private lateinit var bucket: String

    fun createPresignedUrl(key: String, contentType: String): PresignedUrlResponse {
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest {
                it.bucket(bucket)
                    .key(key)
                    .contentType(contentType)
            }
            .build()

        val url = s3Presigner.presignPutObject(presignRequest).url().toString()
        return PresignedUrlResponse(url, key)
    }
}