package com.lineinc.erp.api.server.shared.util;

import java.util.List;
import java.util.function.Function;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;

/**
 * 공통 검증 유틸리티 클래스
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // 유틸리티 클래스 인스턴스화 방지
    }

    /**
     * 메인 담당자가 정확히 1명 존재하는지 검증
     * 
     * @param <T>             요청 DTO 타입
     * @param requests        검증할 요청 리스트
     * @param isMainExtractor 메인 여부를 추출하는 함수
     * @throws IllegalArgumentException 메인 담당자가 1명이 아닌 경우
     */
    public static <T> void validateMainContactExists(final List<T> requests,
            final Function<T, Boolean> isMainExtractor) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        final long mainCount = requests.stream()
                .filter(isMainExtractor::apply)
                .count();

        if (mainCount != 1) {
            throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
        }
    }
}
