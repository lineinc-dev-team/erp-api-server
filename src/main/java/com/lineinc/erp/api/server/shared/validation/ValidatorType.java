package com.lineinc.erp.api.server.shared.validation;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import lombok.Getter;

/**
 * 유효성 검사 유형을 정의하는 열거형(enum) 클래스입니다.
 * 각 타입에 해당하는 오류 메시지를 {@link ValidationMessages}에서 가져와 연결합니다.
 */
@Getter
public enum ValidatorType {

    /**
     * URL 형식 검사
     */
    URL(ValidationMessages.INVALID_URL),

    /**
     * 휴대폰 번호 형식 검사 (예: 010-1234-5678)
     */
    PHONE(ValidationMessages.INVALID_PHONE),

    /**
     * 사업자등록번호 형식 검사 (예: 123-45-67890)
     */
    BUSINESS_NUMBER(ValidationMessages.INVALID_BUSINESS_NUMBER),

    /**
     * 유선 전화번호 형식 검사 (예: 02-123-4567)
     */
    LANDLINE_NUMBER(ValidationMessages.INVALID_LANDLINE),

    /**
     * 휴대폰 또는 유선 전화번호 형식 중 하나 허용
     */
    PHONE_OR_LANDLINE(ValidationMessages.INVALID_PHONE_OR_LANDLINE);

    private final String message;

    ValidatorType(String message) {
        this.message = message;
    }

}
