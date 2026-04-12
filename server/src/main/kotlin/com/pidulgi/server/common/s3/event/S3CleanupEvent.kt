package com.pidulgi.server.common.s3.event

data class S3CleanupEvent(

    val keys: List<String>
)