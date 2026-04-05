package com.pidulgi.server.common.s3

data class S3CleanupEvent(

    val keys: List<String>
)
