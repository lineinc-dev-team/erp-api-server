package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

/**
 * 노무명세서 조회 요청 DTO
 */
public record LaborPayrollSearchRequest(
        Long laborId, // 노무인력 ID (선택사항)
        LocalDate startDate, // 조회 시작 날짜
        LocalDate endDate, // 조회 종료 날짜
        Long siteId, // 현장 ID (선택사항)
        Long siteProcessId, // 공정 ID (선택사항)
        String laborType // 노무인력 타입 (선택사항)
) {
}
