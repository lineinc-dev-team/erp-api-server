package com.lineinc.erp.api.server.interfaces.rest.v2.client.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.clientcompany.service.v2.ClientCompanyV2Service;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/client-companies")
@RequiredArgsConstructor
@Tag(name = "발주처 관리 V2")
public class ClientCompanyV2Controller extends BaseController {

    private final ClientCompanyV2Service clientCompanyV2Service;

    @Operation(summary = "발주처 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_CLIENT_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<ClientCompanyChangeHistoryResponse>>> getClientCompanyChangeHistoriesWithPaging(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest) {
        final Page<ClientCompanyChangeHistoryResponse> page = clientCompanyV2Service
                .getClientCompanyChangeHistoriesWithPaging(
                        id, PageableUtils.createPageable(pageRequest, sortRequest));
        return SuccessResponse.ok(PagingResponse.from(page));
    }
}