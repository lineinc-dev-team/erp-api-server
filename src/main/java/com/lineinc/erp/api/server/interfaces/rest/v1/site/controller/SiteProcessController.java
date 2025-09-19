package com.lineinc.erp.api.server.interfaces.rest.v1.site.controller;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/site-process")
@RequiredArgsConstructor
@Tag(name = "현장 공정 관리", description = "현장 공정 관련 API")
public class SiteProcessController {

    private final SiteProcessService siteProcessService;

    @Operation(summary = "현장 공정 키워드 검색", description = "현장 공정 키워드로 간단한 검색을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<SiteProcessResponse.SiteProcessSimpleResponse>>> searchSiteProcessByKeyword(
            @Valid final SortRequest sortRequest,
            @Valid final PageRequest pageRequest,
            @RequestParam(required = false) final Long siteId,
            @RequestParam(required = false) final String keyword) {
        final Slice<SiteProcessResponse.SiteProcessSimpleResponse> slice = siteProcessService.searchSiteProcessByName(
                siteId,
                keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
