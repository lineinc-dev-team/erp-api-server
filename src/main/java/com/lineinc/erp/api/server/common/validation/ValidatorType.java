package com.lineinc.erp.api.server.common.validation;

/**
 * 검증 타입을 정의하는 열거형(enum) 클래스입니다.
 * 여러 검증 로직에서 타입 구분용으로 사용됩니다.
 */
public enum ValidatorType {

    /**
     * HTTP 또는 HTTPS URL 형식 검증
     */
    URL,

    /**
     * 휴대폰 번호 형식 검증
     * 예: 010-1234-5678
     */
    PHONE,

    /**
     * 이메일 형식 검증
     */
    EMAIL,

    /**
     * 사업자등록번호 형식 검증
     */
    BUSINESS_NUMBER,

    /**
     * 사용자 정의 추가 검증 타입
     */
    CUSTOM
}
