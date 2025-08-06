package com.lineinc.erp.api.server.presentation.v1.outsourcing.controller;

import com.lineinc.erp.api.server.application.outsourcing.OutsourcingCompanyService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.OutsourcingCompanyDefaultDeductionsResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.OutsourcingCompanyDetailResponse;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.OutsourcingCompanyTypeResponse;
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
public class OutsourcingCompanyController {

    private final OutsourcingCompanyService outsourcingCompanyService;

    @Operation(summary = "공제 항목 목록 조회", description = "공제 항목 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/default-deductions")
    public ResponseEntity<SuccessResponse<List<OutsourcingCompanyDefaultDeductionsResponse>>> getDeductionItems() {
        List<OutsourcingCompanyDefaultDeductionsResponse> responseList = Arrays.stream(OutsourcingCompanyDefaultDeductionsType.values())
                .map(dd -> new OutsourcingCompanyDefaultDeductionsResponse(dd.name(), dd.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "구분 목록 조회", description = "외주업체 구분 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/types")
    public ResponseEntity<SuccessResponse<List<OutsourcingCompanyTypeResponse>>> getSeparations() {
        List<OutsourcingCompanyTypeResponse> responseList = Arrays.stream(OutsourcingCompanyType.values())
                .map(type -> new OutsourcingCompanyTypeResponse(type.name(), type.getLabel()))
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
            @ApiResponse(responseCode = "404", description = "외주업체를 찾을 수 없음", content = @Content()),
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
    public ResponseEntity<SuccessResponse<OutsourcingCompanyDetailResponse>> getOutsourcingCompanyById(
            @PathVariable Long id
    ) {
        OutsourcingCompanyDetailResponse response = outsourcingCompanyService.getOutsourcingCompanyById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

}

