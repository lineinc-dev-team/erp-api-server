package com.lineinc.erp.api.server.shared.util;

import org.springframework.util.StringUtils;

public class PrivacyMaskingUtils {

    /**
     * 주민등록번호 마스킹 처리
     * 앞 6자리는 그대로, 뒤 7자리는 첫 번째만 표시하고 나머지는 *로 마스킹
     * 
     * @param residentNumber 주민등록번호 (예: 860101-1234567)
     * @return 마스킹된 주민등록번호 (예: 860101-1******)
     */
    public static String maskResidentNumber(String residentNumber) {
        if (!StringUtils.hasText(residentNumber)) {
            return "";
        }

        // 하이픈 제거
        String cleanNumber = residentNumber.replace("-", "");

        if (cleanNumber.length() != 13) {
            return residentNumber; // 유효하지 않은 형식이면 원본 반환
        }

        // 앞 6자리 + 하이픈 + 뒤 7자리 중 첫 번째만 + 나머지 6자리는 *
        String front = cleanNumber.substring(0, 6);
        String back = cleanNumber.substring(6);
        String firstDigit = back.substring(0, 1);
        String masked = "*".repeat(6);

        return front + "-" + firstDigit + masked;
    }

}
