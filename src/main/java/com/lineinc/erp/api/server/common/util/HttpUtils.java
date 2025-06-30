package com.lineinc.erp.api.server.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HttpUtils
 * - HTTP 요청 관련 유틸리티 메서드를 제공하는 클래스입니다.
 */
public class HttpUtils {

    /**
     * 클라이언트의 실제 IP 주소를 추출합니다.
     * 일반적으로 프록시나 로드밸런서를 거치는 경우가 많기 때문에 여러 헤더를 순서대로 검사합니다.
     * <p>
     * 우선순위:
     * 1. X-Forwarded-For
     * 2. X-Real-IP
     * 3. Proxy-Client-IP
     * 4. WL-Proxy-Client-IP
     * 5. request.getRemoteAddr()
     *
     * @param request HttpServletRequest
     * @return 추정된 클라이언트 IP 주소
     */
    public static String getClientIp(HttpServletRequest request) {
        // 프록시 서버를 거친 원래 클라이언트 IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 일부 프록시 또는 로드밸런서에서 사용하는 헤더
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 웹로직, 일부 WAS에서 사용하는 헤더
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 웹로직에서 사용하는 또 다른 헤더
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 위 모든 헤더가 없을 경우 기본 값
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For에 다중 IP가 있을 경우, 첫 번째 IP가 실제 클라이언트 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}