package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementDetailV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 V2 상세 항목 응답")
public record SteelManagementDetailV2Response(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "외주업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "타입", example = "INCOMING") SteelManagementDetailV2Type typeCode,
        @Schema(description = "타입명", example = "입고") String type,
        @Schema(description = "품명", example = "철근") String name,
        @Schema(description = "규격", example = "D10") String specification,
        @Schema(description = "무게 (톤)", example = "5.6") Double weight,
        @Schema(description = "본", example = "10") Integer count,
        @Schema(description = "총무게 (톤)", example = "56.0") Double totalWeight,
        @Schema(description = "단가 (원)", example = "12000") Long unitPrice,
        @Schema(description = "금액 (원)", example = "672000") Long amount,
        @Schema(description = "부가세 (원)", example = "120000") Long vat,
        @Schema(description = "합계 (원)", example = "792000") Long total,
        @Schema(description = "구분", example = "PURCHASE") SteelManagementDetailV2Category category,
        @Schema(description = "구분명", example = "구매") String categoryName,
        @Schema(description = "파일 URL") String fileUrl,
        @Schema(description = "원본 파일명") String originalFileName,
        @Schema(description = "메모") String memo) {
    public static SteelManagementDetailV2Response from(final SteelManagementDetailV2 entity) {
        return new SteelManagementDetailV2Response(
                entity.getId(),
                entity.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(entity.getOutsourcingCompany())
                        : null,
                entity.getType(),
                entity.getType() != null ? entity.getType().getLabel() : null,
                entity.getName(),
                entity.getSpecification(),
                entity.getWeight(),
                entity.getCount(),
                entity.getTotalWeight(),
                entity.getUnitPrice(),
                entity.getAmount(),
                entity.getVat(),
                entity.getTotal(),
                entity.getCategory(),
                entity.getCategory() != null ? entity.getCategory().getLabel() : null,
                entity.getFileUrl(),
                entity.getOriginalFileName(),
                entity.getMemo());
    }
}
