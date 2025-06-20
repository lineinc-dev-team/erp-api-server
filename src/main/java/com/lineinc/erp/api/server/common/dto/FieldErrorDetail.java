package com.lineinc.erp.api.server.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 유효성 검사 실패 시 필드별 상세 정보
 */
@Getter
@AllArgsConstructor
public class FieldErrorDetail {
    private String field;   // 유효성 검사 실패 필드명
    private String message; // 에러 메시지
}