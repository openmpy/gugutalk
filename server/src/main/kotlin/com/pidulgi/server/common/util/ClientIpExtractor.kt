package com.pidulgi.server.common.util

import jakarta.servlet.http.HttpServletRequest

object ClientIpExtractor {

    private val IP_HEADERS = listOf(
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR",
        "X-Real-IP"
    )

    /**
     * 서블릿 요청에서 클라이언트 실제 IP를 추출합니다.
     * 프록시/로드밸런서 환경을 고려하여 여러 헤더를 순서대로 확인합니다.
     */
    fun extract(request: HttpServletRequest): String {
        for (header in IP_HEADERS) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrBlank() && !ip.equals("unknown", ignoreCase = true)) {
                return ip.split(",").first().trim()
            }
        }
        return request.remoteAddr
    }

    /**
     * X-Forwarded-For 체인에서 모든 IP 목록을 반환합니다.
     * [0]: 원본 클라이언트, 이후: 거쳐온 프록시들
     */
    fun extractChain(request: HttpServletRequest): List<String> {
        val forwarded = request.getHeader("X-Forwarded-For")
        if (!forwarded.isNullOrBlank() && !forwarded.equals("unknown", ignoreCase = true)) {
            return forwarded.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
        return listOf(request.remoteAddr)
    }

    /**
     * IP가 유효한 IPv4 또는 IPv6 형식인지 확인합니다.
     */
    fun isValidIp(ip: String): Boolean {
        val ipv4Regex = Regex("""^(\d{1,3}\.){3}\d{1,3}$""")
        val ipv6Regex = Regex("""^[0-9a-fA-F:]+$""")
        return ip.matches(ipv4Regex) || ip.matches(ipv6Regex)
    }

    /**
     * 추출된 IP가 유효한지 검증 후 반환합니다. 유효하지 않으면 null을 반환합니다.
     */
    fun extractValidated(request: HttpServletRequest): String? {
        val ip = extract(request)
        return if (isValidIp(ip)) ip else null
    }
}