package com.pidulgi.server.report.service.extension

import com.pidulgi.server.report.dto.response.ReportImageResponse
import com.pidulgi.server.report.entity.ReportImage

fun ReportImage.toResponses(url: String) = ReportImageResponse(

    imageId = this.id,
    index = this.sortOrder,
    url = url,
)