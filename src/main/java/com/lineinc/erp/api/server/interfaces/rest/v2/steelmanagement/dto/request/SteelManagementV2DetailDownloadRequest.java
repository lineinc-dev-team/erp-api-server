package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "강재수불부 V2 상세 엑셀 다운로드 요청")
public record SteelManagementV2DetailDownloadRequest(
        @Schema(description = "다운로드할 필드들을 쉼표로 구분 (미입력시 전체 필드 다운로드)", example = EXAMPLE_FIELDS) String fields) {
    private static final String EXAMPLE_FIELDS = "incomingDate,outgoingDate,salesDate,name,specification,weight,count,totalWeight,unitPrice,amount,vat,total,category,outsourcingCompanyName,createdAt,originalFileName,memo";
    public static final List<String> ALLOWED_FIELDS = List.of(EXAMPLE_FIELDS.split(","));

    /**
     * 사용할 필드 목록을 반환 (fields가 null이거나 빈 문자열이면 전체 필드 반환)
     */
    public List<String> getFieldsToUse() {
        if (fields == null || fields.trim().isEmpty()) {
            return ALLOWED_FIELDS;
        }
        return List.of(fields.split(","));
    }
}
