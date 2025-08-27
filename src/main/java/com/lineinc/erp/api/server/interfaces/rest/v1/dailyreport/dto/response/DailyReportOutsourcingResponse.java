package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcing;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractWorkerResponse.ContractWorkerSimpleResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 외주 응답")
public record DailyReportOutsourcingResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "작업내용", example = "기초 콘크리트 타설") String workContent,

        @Schema(description = "공수", example = "8.0") Double workQuantity,

        @Schema(description = "구분값", example = "기초공사") String category,

        @Schema(description = "비고", example = "오전 작업") String memo,

        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,

        @Schema(description = "외주업체계약 인력명", example = "김철수") ContractWorkerSimpleResponse outsourcingCompanyWorker) {

    public static DailyReportOutsourcingResponse from(DailyReportOutsourcing outsourcing) {
        return new DailyReportOutsourcingResponse(
                outsourcing.getId(),
                outsourcing.getWorkContent(),
                outsourcing.getWorkQuantity(),
                outsourcing.getCategory(),
                outsourcing.getMemo(),
                outsourcing.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(outsourcing.getOutsourcingCompany())
                        : null,
                outsourcing.getOutsourcingCompanyContractWorker() != null
                        ? ContractWorkerSimpleResponse.from(outsourcing.getOutsourcingCompanyContractWorker())
                        : null);
    }
}
