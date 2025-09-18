package com.lineinc.erp.api.server.shared.util;

/**
 * 데이터 포맷팅 관련 유틸리티 클래스
 */
public final class FormatUtils {

    private FormatUtils() {
        // 인스턴스 생성 방지
    }

    /**
     * Boolean 값을 "Y"/"N" 문자열로 변환합니다.
     * 
     * @param value Boolean 값
     * @return true면 "Y", false면 "N", null이면 null
     */
    public static String toYesNo(final Boolean value) {
        return value != null ? (value ? "Y" : "N") : null;
    }
}
