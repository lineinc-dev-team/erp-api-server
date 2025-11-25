package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.batch.enums.BatchName;
import com.lineinc.erp.api.server.domain.dashboard.service.DashboardService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.request.SiteProcessMonthlyCostsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.BatchNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.DashboardBatchExecutionTimeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.DashboardSiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.SiteMonthlyCostResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.SiteMonthlyCostsResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 대시보드 관련 API.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "대시보드")
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    @Operation(summary = "현장 목록 조회")
    @GetMapping("/sites")
    public ResponseEntity<SuccessResponse<List<DashboardSiteResponse>>> getDashboardSites(
            @RequestParam(required = false) final String keyword,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final List<DashboardSiteResponse> responses =
                dashboardService.getDashboardSites(user.getUserId(), keyword);
        return ResponseEntity.ok(SuccessResponse.of(responses));
    }

    @Operation(summary = "현장별 비용 총합 조회 (본사직원 전용)")
    @GetMapping("/site-costs")
    public ResponseEntity<SuccessResponse<List<SiteMonthlyCostsResponse>>> getSiteCosts(
            @AuthenticationPrincipal final CustomUserDetails user) {
        final List<SiteMonthlyCostsResponse> responses =
                dashboardService.getSiteMonthlyCosts(user.getUserId());
        return ResponseEntity.ok(SuccessResponse.of(responses));
    }

    @Operation(summary = "현장 및 공정별 월별 비용 목록 조회")
    @GetMapping("/monthly-costs")
    public ResponseEntity<SuccessResponse<List<SiteMonthlyCostResponse>>> getSiteProcessMonthlyCosts(
            @ModelAttribute final SiteProcessMonthlyCostsRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final List<SiteMonthlyCostResponse> responses = dashboardService.getSiteProcessMonthlyCosts(
                user.getUserId(), request.siteId(), request.siteProcessId());
        return ResponseEntity.ok(SuccessResponse.of(responses));
    }

    @Operation(summary = "배치 이름 목록 조회")
    @GetMapping("/batch-names")
    public ResponseEntity<SuccessResponse<List<BatchNameResponse>>> getBatchNames() {
        final List<BatchNameResponse> responses =
                java.util.Arrays.stream(BatchName.values()).map(BatchNameResponse::new).toList();
        return ResponseEntity.ok(SuccessResponse.of(responses));
    }

    @Operation(summary = "배치 최근 실행시간 조회")
    @GetMapping("/batch-latest-execution-time")
    public ResponseEntity<SuccessResponse<DashboardBatchExecutionTimeResponse>> getBatchExecutionTime(
            @RequestParam(required = true) final BatchName batchName) {
        final DashboardBatchExecutionTimeResponse response =
                dashboardService.getBatchExecutionTime(batchName);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
