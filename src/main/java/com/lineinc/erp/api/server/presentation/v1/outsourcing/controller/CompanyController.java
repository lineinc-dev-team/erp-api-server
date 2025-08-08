package com.lineinc.erp.api.server.presentation.v1.outsourcing.controller;

import com.lineinc.erp.api.server.common.response.SliceResponse;
import com.lineinc.erp.api.server.common.response.SliceInfo;
import org.springframework.data.domain.Slice;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyChangeHistoryResponse;

import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyDownloadRequest;
import com.lineinc.erp.api.server.common.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.common.util.ResponseHeaderUtils;
import org.apache.poi.ss.usermodel.Workbook;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.lineinc.erp.api.server.application.outsourcing.OutsourcingCompanyService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.DeleteOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyResponse;
import org.springframework.data.domain.Page;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyDefaultDeductionsResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyDetailResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outsourcing-companies")
@RequiredArgsConstructor
@Tag(name = "Outsourcing Companies", description = "외주업체 관련 API")
public class CompanyController {

    private final OutsourcingCompanyService outsourcingCompanyService;

    @Operation(
            summary = "외주업체 목록 조회",
            description = "등록된 모든 외주업체 정보를 반환합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "외주업체 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<CompanyResponse>>> getAllOutsourcingCompanies(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid OutsourcingCompanyListRequest request
    ) {
        Page<CompanyResponse> page = outsourcingCompanyService.getAllOutsourcingCompanies(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(summary = "공제 항목 목록 조회", description = "공제 항목 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/default-deductions")
    public ResponseEntity<SuccessResponse<List<CompanyDefaultDeductionsResponse>>> getDeductionItems() {
        List<CompanyDefaultDeductionsResponse> responseList = Arrays.stream(OutsourcingCompanyDefaultDeductionsType.values())
                .map(dd -> new CompanyDefaultDeductionsResponse(dd.name(), dd.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "구분 목록 조회", description = "외주업체 구분 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/types")
    public ResponseEntity<SuccessResponse<List<CompanyTypeResponse>>> getSeparations() {
        List<CompanyTypeResponse> responseList = Arrays.stream(OutsourcingCompanyType.values())
                .map(type -> new CompanyTypeResponse(type.name(), type.getLabel()))
                .toList();

        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(
            summary = "외주업체 등록",
            description = "외주업체 정보를 등록합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createOutsourcingCompany(
            @Valid @RequestBody OutsourcingCompanyCreateRequest request
    ) {
        outsourcingCompanyService.createOutsourcingCompany(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "외주업체 상세 조회", description = "외주업체 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "외주업체를 찾을 수 없음", content = @Content()),
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<CompanyDetailResponse>> getOutsourcingCompanyById(
            @PathVariable Long id
    ) {
        CompanyDetailResponse response = outsourcingCompanyService.getOutsourcingCompanyById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(
            summary = "외주업체 수정",
            description = "특정 외주업체 정보를 수정합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "외주업체를 찾을 수 없음"),
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateOutsourcingCompany(
            @PathVariable Long id,
            @Valid @RequestBody OutsourcingCompanyUpdateRequest request
    ) {
        outsourcingCompanyService.updateOutsourcingCompany(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "외주업체 삭제",
            description = "하나 이상의 외주업체 ID를 받아 해당 외주업체를 삭제합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "외주업체 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "외주업체를 찾을 수 없음"),
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteOutsourcingCompanies(@RequestBody DeleteOutsourcingCompaniesRequest request) {
        outsourcingCompanyService.deleteOutsourcingCompanies(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "외주업체 엑셀 다운로드",
            description = "검색 조건에 맞는 외주업체 목록을 엑셀 파일로 다운로드합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.VIEW)
    public void downloadOutsourcingCompaniesExcel(
            @Valid SortRequest sortRequest,
            @Valid OutsourcingCompanyListRequest request,
            @Valid OutsourcingCompanyDownloadRequest companyDownloadRequest,
            HttpServletResponse response
    ) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(companyDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, OutsourcingCompanyDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "외주업체 목록.xlsx");

        try (Workbook workbook = outsourcingCompanyService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed
        )) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "외주업체 변경 이력 조회", description = "특정 외주업체의 변경 히스토리를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<CompanyChangeHistoryResponse>>> getOutsourcingCompanyChangeHistories(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest
    ) {
        Slice<CompanyChangeHistoryResponse> slice = outsourcingCompanyService.getOutsourcingCompanyChangeHistories(
                id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );
        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "외주업체 이름 키워드 검색", description = "외주업체 이름으로 간단한 검색을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<CompanyResponse.CompanySimpleResponse>>> searchClientCompanyByName(
            @Valid SortRequest sortRequest,
            @Valid PageRequest pageRequest,
            @RequestParam(required = false) String keyword
    ) {
        Slice<CompanyResponse.CompanySimpleResponse> slice = outsourcingCompanyService.searchByName(keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())
        ));
    }
}



