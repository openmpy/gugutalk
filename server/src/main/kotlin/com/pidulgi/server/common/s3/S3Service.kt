package com.pidulgi.server.common.s3

import com.pidulgi.server.common.s3.PresignedUrlsResponse.PresignedUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class S3Service(

    @Value("\${s3.bucket}") private val bucket: String,

    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
) {

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

    fun getPresignedUrl(key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()
        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .getObjectRequest(getObjectRequest)
            .build()
        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    fun delete(key: String) {
        try {
            val request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()

            s3Client.deleteObject(request)
        } catch (e: Exception) {
            throw RuntimeException("S3 삭제 실패 - key: $key", e)
        }
    }

    fun deleteAll(keys: List<String>) {
        if (keys.isEmpty()) {
            return
        }

        keys.chunked(1000).forEach { chunk ->
            val objects = chunk.map { key ->
                ObjectIdentifier.builder().key(key).build()
            }

            val delete = Delete.builder().objects(objects).build()

            val request = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(delete)
                .build()

            try {
                s3Client.deleteObjects(request)
            } catch (e: Exception) {
                throw RuntimeException("S3 배치 삭제 실패 - keys: $chunk", e)
            }
        }
    }
}