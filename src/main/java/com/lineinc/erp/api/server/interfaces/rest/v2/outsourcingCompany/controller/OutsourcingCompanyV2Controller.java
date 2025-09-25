package com.lineinc.erp.api.server.interfaces.rest.v2.outsourcingcompany.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v2.OutsourcingCompanyV2Service;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyChangeHistoryResponse;
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
@RequestMapping("/api/v2/outsourcing-companies")
@RequiredArgsConstructor
@Tag(name = "외주업체 관리 V2")
public class OutsourcingCompanyV2Controller extends BaseController {

    private final OutsourcingCompanyV2Service outsourcingCompanyV2Service;

    @Operation(summary = "외주업체 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_OUTSOURCING_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<CompanyChangeHistoryResponse>>> getOutsourcingCompanyChangeHistoriesWithPaging(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Page<CompanyChangeHistoryResponse> page = outsourcingCompanyV2Service
                .getOutsourcingCompanyChangeHistoriesWithPaging(
                        id, PageableUtils.createPageable(pageRequest, sortRequest), user.getUserId());
        return SuccessResponse.ok(PagingResponse.from(page));
    }

}
