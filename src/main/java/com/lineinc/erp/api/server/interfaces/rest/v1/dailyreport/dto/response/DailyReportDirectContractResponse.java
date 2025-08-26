package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 직영/계약직 응답")
public record DailyReportDirectContractResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "직종", example = "토목공") String position,

        @Schema(description = "작업내용", example = "기초 콘크리트 타설") String workContent,

        @Schema(description = "단가", example = "50000") Long unitPrice,

        @Schema(description = "공수", example = "8.0") Double workQuantity,

        @Schema(description = "비고", example = "오전 작업") String memo,

        @Schema(description = "인력 정보") LaborNameResponse labor,

        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany) {

    public static DailyReportDirectContractResponse from(DailyReportDirectContract directContract) {
        return new DailyReportDirectContractResponse(
                directContract.getId(),
                directContract.getPosition(),
                directContract.getWorkContent(),
                directContract.getUnitPrice(),
                directContract.getWorkQuantity(),
                directContract.getMemo(),
                directContract.getLabor() != null ? LaborNameResponse.from(directContract.getLabor()) : null,
                directContract.getCompany() != null ? CompanySimpleResponse.from(directContract.getCompany()) : null);
    }
}
