package com.lineinc.erp.api.server.presentation.v1.outsourcing.controller;

import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.ContractStatusResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyContractDefaultDeductionsResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.ContractTypeResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.TaxInvoiceConditionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outsourcing-company-contracts")
@RequiredArgsConstructor
@Tag(name = "Outsourcing Company Contract", description = "외주업체 계약 관련 API")
public class CompanyContractController {
    @Operation(summary = "공제 항목 목록 조회", description = "공제 항목 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/default-deductions")
    public ResponseEntity<SuccessResponse<List<CompanyContractDefaultDeductionsResponse>>> getDeductionItems() {
        List<CompanyContractDefaultDeductionsResponse> responseList = Arrays.stream(OutsourcingCompanyContractDefaultDeductionsType.values())
                .map(dd -> new CompanyContractDefaultDeductionsResponse(dd.name(), dd.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "세금계산서 발행조건 목록 조회", description = "세금계산서 발행조건 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/tax-invoice-conditions")
    public ResponseEntity<SuccessResponse<List<TaxInvoiceConditionResponse>>> getTaxInvoiceConditions() {
        List<TaxInvoiceConditionResponse> responseList = Arrays.stream(OutsourcingCompanyTaxInvoiceConditionType.values())
                .map(condition -> new TaxInvoiceConditionResponse(condition.name(), condition.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "계약 상태 목록 조회", description = "외주업체 계약 상태 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/statuses")
    public ResponseEntity<SuccessResponse<List<ContractStatusResponse>>> getContractStatuses() {
        List<ContractStatusResponse> responseList = Arrays.stream(OutsourcingCompanyContractStatus.values())
                .map(status -> new ContractStatusResponse(status.name(), status.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "계약 구분 목록 조회", description = "외주업체 계약 구분 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/types")
    public ResponseEntity<SuccessResponse<List<ContractTypeResponse>>> getContractTypes() {
        List<ContractTypeResponse> responseList = Arrays.stream(OutsourcingCompanyContractType.values())
                .map(type -> new ContractTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

}
