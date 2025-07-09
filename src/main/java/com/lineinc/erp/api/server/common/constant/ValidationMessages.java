package com.lineinc.erp.api.server.common.constant;

public final class ValidationMessages {

    private ValidationMessages() {
    } // 인스턴스화 방지

    public static final String DEFAULT_INVALID_INPUT = "입력값이 유효하지 않습니다.";
    public static final String INVALID_PROPERTY_REFERENCE = "요청에 잘못된 정렬 조건 또는 필드명이 포함되어 있습니다.";
    public static final String RESOURCE_NOT_FOUND = "요청한 리소스를 찾을 수 없습니다.";
    public static final String PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";
    public static final String UNSUPPORTED_CONTENT_TYPE = "지원하지 않는 콘텐츠 타입입니다.";
    public static final String ACCESS_DENIED = "접근 권한이 없습니다.";
    public static final String FILE_PROCESS_ERROR = "파일 처리 중 오류가 발생했습니다.";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다.";
    public static final String USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    public static final String INVALID_DATE_FORMAT = "날짜 형식이 올바르지 않습니다. (예: yyyy-MM-dd)";
    public static final String INVALID_FIELD = "정의되지 않은 필드입니다.";

}