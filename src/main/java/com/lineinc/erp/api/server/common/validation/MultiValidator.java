package com.lineinc.erp.api.server.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * MultiConstraint 어노테이션에 대한 검증 로직을 구현하는 클래스입니다.
 * ValidatorType에 따라 다양한 검증을 수행합니다.
 */
public class MultiValidator implements ConstraintValidator<MultiConstraint, String> {

    /**
     * 현재 검증에 사용할 타입
     */
    private ValidatorType type;

    /**
     * 어노테이션 초기화 메서드로, 검증 타입을 받아 저장합니다.
     *
     * @param constraintAnnotation MultiConstraint 어노테이션 인스턴스
     */
    @Override
    public void initialize(MultiConstraint constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    /**
     * 실제 검증 수행 메서드
     *
     * @param value   검증할 문자열 값
     * @param context ConstraintValidatorContext
     * @return 검증 성공 시 true, 실패 시 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 혹은 빈 문자열은 유효하지 않음
        // (필요하다면 별도로 @NotBlank 등과 함께 사용 권장)
        if (value == null || value.isBlank()) {
            return false;
        }

        // 타입별 검증 수행
        switch (type) {
            case URL:
                return isValidUrl(value);
            case PHONE:
                return isValidPhone(value);
            case BUSINESS_NUMBER:
                return isValidBusinessNumber(value);
            case CUSTOM:
                // 커스텀 검증 로직이 필요하면 구현
                return true;
            default:
                return false;
        }
    }

    /**
     * HTTP 또는 HTTPS URL 형식 검증
     *
     * @param value 검증할 문자열
     * @return 정규식 매칭 결과
     */
    private boolean isValidUrl(String value) {
        // 간단한 http/https URL 정규식 패턴
        return value.matches("^(https?)://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=.]+$");
    }

    /**
     * 한국 휴대폰 번호 형식 검증
     *
     * @param value 검증할 문자열
     * @return 정규식 매칭 결과
     */
    private boolean isValidPhone(String value) {
        // 예: 010-1234-5678, 016-123-4567 등
        return value.matches("^01[016789]-\\d{3,4}-\\d{4}$");
    }

    /**
     * 한국 사업자등록번호 형식 검증
     *
     * @param value 검증할 문자열
     * @return 정규식 매칭 결과
     */
    private boolean isValidBusinessNumber(String value) {
        // 예: 123-45-67890
        return value.matches("^\\d{3}-\\d{2}-\\d{5}$");
    }
}