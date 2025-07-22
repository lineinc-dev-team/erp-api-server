package com.lineinc.erp.api.server.common.validation;

/**
 * 검증 타입을 정의하는 열거형(enum) 클래스입니다.
 * 여러 검증 로직에서 타입 구분용으로 사용됩니다.
 */
public enum ValidatorType {

    URL("유효한 URL 형식이 아닙니다."),
    PHONE("유효한 휴대폰 번호 형식이 아닙니다."),
    BUSINESS_NUMBER("유효한 사업자등록번호 형식이 아닙니다."),
    LANDLINE_NUMBER("유효한 유선 전화번호 형식이 아닙니다."),
    PHONE_OR_LANDLINE("유효한 전화번호 형식(휴대폰 또는 유선전화)이 아닙니다.");

    private final String message;

    ValidatorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
