package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "강재수불부 V2 엑셀 다운로드 요청")
public record SteelManagementV2DownloadRequest(
        @NotBlank @Schema(description = "다운로드할 필드들을 쉼표로 구분", example = EXAMPLE_FIELDS) String fields) {
    private static final String EXAMPLE_FIELDS = "id,siteName,processName,incomingOwnMaterial,incomingPurchase,incomingRental,outgoingOwnMaterial,outgoingPurchase,outgoingRental,onSiteStock,scrap,totalInvestmentAmount,onSiteRemainingWeight,createdAt";
    public static final List<String> ALLOWED_FIELDS = List.of(EXAMPLE_FIELDS.split(","));
}
