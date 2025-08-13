package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.ContractDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.ContractListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.DeleteOutsourcingCompanyContractsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyContractDefaultDeductionsResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractCategoryTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractStatusResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractWorkerResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.TaxInvoiceConditionResponse;
import com.lineinc.erp.api.server.shared.dto.PageRequest;
import com.lineinc.erp.api.server.shared.dto.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/outsourcing-company-contracts")
@RequiredArgsConstructor
@Tag(name = "외주업체 계약 관리", description = "외주업체 계약 관련 API")
public class CompanyContractController {

    private final OutsourcingCompanyContractService outsourcingCompanyContractService;

    @Operation(summary = "공제 항목 목록 조회", description = "공제 항목 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/default-deductions")
    public ResponseEntity<SuccessResponse<List<CompanyContractDefaultDeductionsResponse>>> getDeductionItems() {
        List<CompanyContractDefaultDeductionsResponse> responseList = Arrays
                .stream(OutsourcingCompanyContractDefaultDeductionsType.values())
                .map(dd -> new CompanyContractDefaultDeductionsResponse(dd.name(), dd.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "세금계산서 발행조건 목록 조회", description = "세금계산서 발행조건 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/tax-invoice-conditions")
    public ResponseEntity<SuccessResponse<List<TaxInvoiceConditionResponse>>> getTaxInvoiceConditions() {
        List<TaxInvoiceConditionResponse> responseList = Arrays
                .stream(OutsourcingCompanyTaxInvoiceConditionType.values())
                .map(condition -> new TaxInvoiceConditionResponse(condition.name(),
                        condition.getLabel()))
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

    @Operation(summary = "계약 유형 카테고리 목록 조회", description = "외주업체 계약 유형 카테고리 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/category-types")
    public ResponseEntity<SuccessResponse<List<ContractCategoryTypeResponse>>> getContractCategoryTypes() {
        List<ContractCategoryTypeResponse> responseList = Arrays
                .stream(OutsourcingCompanyContractCategoryType.values())
                .map(type -> new ContractCategoryTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "외주업체 계약 등록", description = "외주업체 계약 정보를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @PostMapping
    public ResponseEntity<Void> createOutsourcingCompanyContract(
            @Valid @RequestBody OutsourcingCompanyContractCreateRequest request) {
        outsourcingCompanyContractService.createContract(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "외주계약 리스트 조회", description = "검색 조건에 따라 외주계약 리스트를 페이징하여조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<PagingResponse<ContractListResponse>>> getContractList(
            @Valid ContractListSearchRequest searchRequest,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {

        Page<ContractListResponse> page = outsourcingCompanyContractService.getContractList(
                searchRequest,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "외주업체 계약 상세조회", description = "외주업체 계약 ID로 상세 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "계약을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ContractDetailResponse>> getContractDetail(
            @PathVariable Long id) {
        ContractDetailResponse response = outsourcingCompanyContractService.getContractDetail(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "외주업체 계약 인력 정보 조회", description = "외주업체 계약 ID로 해당 계약의 인력 정보를 Slice로 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "계약을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/workers")
    public ResponseEntity<SuccessResponse<SliceResponse<ContractWorkerResponse>>> getContractWorkers(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {
        Slice<ContractWorkerResponse> slice = outsourcingCompanyContractService.getContractWorkers(
                id, PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "외주업체 계약 엑셀 다운로드", description = "검색 조건에 맞는 외주업체 계약 목록을 엑셀 파일로 다운로드합니다.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()) })
    @GetMapping("/download")
    public void downloadContractsExcel(@Valid SortRequest sortRequest,
            @Valid ContractListSearchRequest request,
            @Valid ContractDownloadRequest downloadRequest,
            HttpServletResponse response) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(downloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, ContractDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "외주업체 계약 목록.xlsx");

        try (Workbook workbook = outsourcingCompanyContractService.downloadExcel(request,
                PageableUtils.parseSort(sortRequest.sort()), parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "외주업체 계약 삭제", description = "하나 이상의 외주업체 계약 ID를 받아 해당 계약을 삭제합니다")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "외주업체 계약 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "외주업체 계약을 찾을 수 없음"), })
    @DeleteMapping
    public ResponseEntity<Void> deleteOutsourcingCompanyContracts(
            @RequestBody DeleteOutsourcingCompanyContractsRequest request) {
        outsourcingCompanyContractService.deleteContracts(request);
        return ResponseEntity.ok().build();
    }

}
