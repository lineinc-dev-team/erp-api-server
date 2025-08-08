package com.lineinc.erp.api.server.shared.validation;

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
        if (value == null || value.isBlank()) {
            return true;
        }

        boolean result = switch (type) {
            case URL -> isValidUrl(value);
            case PHONE -> isValidPhone(value);
            case LANDLINE_NUMBER -> isValidLandlineNumber(value);
            case PHONE_OR_LANDLINE -> isValidPhone(value) || isValidLandlineNumber(value);
            case BUSINESS_NUMBER -> isValidBusinessNumber(value);
        };

        if (!result) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(type.getMessage())
                    .addConstraintViolation();
        }

        return result;
    }

    /**
     * HTTP 또는 HTTPS URL 형식 검증
     *
     * @param value 검증할 문자열
     * @return 정규식 매칭 결과
     */
    private boolean isValidUrl(String value) {
        // 간단한 http/https URL 정규식 패턴
        return value.matches("^(http|https)://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=.]+$");
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

    /**
     * 한국 유선 전화번호 형식 검증
     *
     * @param value 검증할 문자열
     * @return 정규식 매칭 결과
     */
    private boolean isValidLandlineNumber(String value) {
        // 예: 02-123-4567 또는 031-1234-5678
        return value.matches("^0\\d{1,2}-\\d{3,4}-\\d{4}$");
    }
}