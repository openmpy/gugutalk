package com.pidulgi.server.report.repository

import com.pidulgi.server.report.entity.Report
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<Report, Long>