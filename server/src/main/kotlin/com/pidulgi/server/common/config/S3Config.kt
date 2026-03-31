package com.pidulgi.server.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config {

    @Value("\${s3.access-key}")
    private lateinit var accessKey: String

    @Value("\${s3.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun s3Client(): S3Client {
        val basicCredentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val basicCredentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Presigner.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
            .build()
    }
}