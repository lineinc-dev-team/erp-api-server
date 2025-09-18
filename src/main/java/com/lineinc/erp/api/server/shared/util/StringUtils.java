package com.lineinc.erp.api.server.shared.util;

/**
 * 문자열 관련 유틸리티 클래스
 */
public final class StringUtils {

    private StringUtils() {
        // 인스턴스화 방지
    }

    /**
     * 두 문자열을 공백으로 연결
     * null이나 빈 문자열은 자동으로 처리
     * 
     * @param str1 첫 번째 문자열
     * @param str2 두 번째 문자열
     * @return 공백으로 연결된 문자열
     */
    public static String joinWithSpace(final String str1, final String str2) {
        if (str1 == null || str1.isBlank()) {
            return str2 != null ? str2 : "";
        }
        if (str2 == null || str2.isBlank()) {
            return str1;
        }
        return str1 + " " + str2;
    }

    /**
     * 여러 문자열을 공백으로 연결
     * null이나 빈 문자열은 자동으로 제외
     * 
     * @param strings 연결할 문자열들
     * @return 공백으로 연결된 문자열
     */
    public static String joinWithSpace(final String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }

        return java.util.Arrays.stream(strings)
                .filter(str -> str != null && !str.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    /**
     * 두 문자열을 슬래시(/)로 연결
     * null이나 빈 문자열은 자동으로 처리
     * 
     * @param str1 첫 번째 문자열
     * @param str2 두 번째 문자열
     * @return 슬래시로 연결된 문자열
     */
    public static String joinWithSlash(final String str1, final String str2) {
        if (str1 == null || str1.isBlank()) {
            return str2 != null ? str2 : "";
        }
        if (str2 == null || str2.isBlank()) {
            return str1;
        }
        return str1 + " / " + str2;
    }
}
