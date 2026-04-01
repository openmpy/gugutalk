package com.pidulgi.server.report.repository

import com.pidulgi.server.report.entity.ReportImage
import org.springframework.data.jpa.repository.JpaRepository

interface ReportImageRepository : JpaRepository<ReportImage, Long>