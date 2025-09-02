package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse.ContractDriverSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractEquipmentResponse.ContractEquipmentSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "출역일보 외주업체 장비 응답")
public record DailyReportEquipmentResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "작업내용", example = "기초 굴착 작업") String workContent,

        @Schema(description = "단가", example = "50000") Long unitPrice,

        @Schema(description = "작업시간", example = "8.0") Double workHours,

        @Schema(description = "비고", example = "정상 작동") String memo,

        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,

        @Schema(description = "기사 정보") ContractDriverSimpleResponse outsourcingCompanyContractDriver,

        @Schema(description = "장비 정보") ContractEquipmentSimpleResponse outsourcingCompanyContractEquipment,

        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportEquipmentResponse from(DailyReportOutsourcingEquipment equipment) {
        return new DailyReportEquipmentResponse(
                equipment.getId(),
                equipment.getWorkContent(),
                equipment.getUnitPrice(),
                equipment.getWorkHours(),
                equipment.getMemo(),
                equipment.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(equipment.getOutsourcingCompany())
                        : null,
                equipment.getOutsourcingCompanyContractDriver() != null
                        ? ContractDriverSimpleResponse.from(equipment.getOutsourcingCompanyContractDriver())
                        : null,
                equipment.getOutsourcingCompanyContractEquipment() != null
                        ? ContractEquipmentSimpleResponse.from(equipment.getOutsourcingCompanyContractEquipment())
                        : null,
                equipment.getCreatedAt(),
                equipment.getUpdatedAt());
    }
}
