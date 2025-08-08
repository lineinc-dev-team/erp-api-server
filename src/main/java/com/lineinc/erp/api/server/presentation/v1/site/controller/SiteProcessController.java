package com.lineinc.erp.api.server.presentation.v1.site.controller;

import com.lineinc.erp.api.server.application.site.SiteProcessService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.*;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.siteprocess.SiteProcessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/site-process")
@RequiredArgsConstructor
@Tag(name = "Site Processes", description = "현장 공정 관련 API")
public class SiteProcessController {

    private final SiteProcessService siteProcessService;

    @Operation(summary = "현장 공정 키워드 검색", description = "현장 공정 키워드로 간단한 검색을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<SiteProcessResponse.SiteProcessSimpleResponse>>> searchSiteProcessByKeyword(
            @Valid SortRequest sortRequest,
            @Valid PageRequest pageRequest,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) String keyword
    ) {
        Slice<SiteProcessResponse.SiteProcessSimpleResponse> slice = siteProcessService.searchSiteProcessByName(
                siteId,
                keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())
        ));
    }
}
