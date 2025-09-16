package com.lineinc.erp.api.server.interfaces.rest.v2.client.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.client.service.CompanyService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/client-companies")
@RequiredArgsConstructor
@Tag(name = "발주처 관리 V2", description = "발주처 관련 V2 API")
public class ClientCompanyV2Controller {

    private final CompanyService companyService;

    @Operation(summary = "발주처 변경 이력 조회", description = "특정 발주처의 변경 히스토리를 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_CLIENT_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<ClientCompanyChangeHistoryResponse>>> getClientCompanyChangeHistories(
            @PathVariable Long id, @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {
        Page<ClientCompanyChangeHistoryResponse> page = companyService.getClientCompanyChangeHistoriesWithPaging(id,
                PageableUtils.createPageable(pageRequest.page(),
                        pageRequest.size(),
                        sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

}
