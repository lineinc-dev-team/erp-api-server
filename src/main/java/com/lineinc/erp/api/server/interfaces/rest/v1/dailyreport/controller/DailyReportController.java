package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType;
import com.lineinc.erp.api.server.domain.dailyreport.service.v1.DailyReportService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractOutsourcingUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportInputStatusUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportMainProcessUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportMaterialStatusUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingConstructionUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportWorkUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractOutsourcingResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEmployeeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEvidenceFileResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFileResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFuelResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportInputStatusResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportMainProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportMaterialStatusResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportOutsourcingCompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportWorkResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/daily-reports")
@RequiredArgsConstructor
@Tag(name = "출역일보")
public class DailyReportController extends BaseController {

    private final DailyReportService dailyReportService;

    @Operation(summary = "출역일보 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<DailyReportListResponse>>> searchDailyReports(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportListSearchRequest searchRequest) {
        final Page<DailyReportListResponse> page = dailyReportService.searchDailyReports(
                user.getUserId(),
                searchRequest,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "출역일보 등록")
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createDailyReport(
            @Valid @RequestBody final DailyReportCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        dailyReportService.createDailyReport(request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 수정")
    @PatchMapping
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReport(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportUpdateRequest request) {
        dailyReportService.updateDailyReport(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 상세 조회")
    @GetMapping("/detail")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<DailyReportDetailResponse>> getDailyReportDetail(
            @Valid final DailyReportSearchRequest request) {
        final DailyReportDetailResponse response = dailyReportService.getDailyReportDetail(request);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "출역일보 직원정보 조회")
    @GetMapping("/employees")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportEmployeeResponse>>> searchDailyReportEmployees(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportEmployeeResponse> slice = dailyReportService.searchDailyReportEmployees(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 직원정보 수정")
    @PatchMapping("/employees")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportEmployee(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportEmployeeUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        dailyReportService.updateDailyReportEmployees(searchRequest, request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 직영/용역 수정")
    @PatchMapping("/direct-contracts")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportDirectContract(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportDirectContractUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        dailyReportService.updateDailyReportDirectContracts(searchRequest, request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 직영/용역 외주 수정")
    @PatchMapping("/direct-contract-outsourcings")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportDirectContractOutsourcing(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportDirectContractOutsourcingUpdateRequest request) {
        dailyReportService.updateDailyReportDirectContractOutsourcings(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 장비 수정")
    @PatchMapping("/equipments")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportEquipment(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportEquipmentUpdateRequest request) {
        dailyReportService.updateDailyReportEquipments(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 파일 수정")
    @PatchMapping("/files")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportFile(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportFileUpdateRequest request) {
        dailyReportService.updateDailyReportFiles(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 직영/용역 조회")
    @GetMapping("/direct-contracts")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportDirectContractResponse>>> searchDailyReportDirectContracts(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportDirectContractResponse> slice = dailyReportService.searchDailyReportDirectContracts(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 직영/용역 외주 조회")
    @GetMapping("/direct-contract-outsourcings")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportDirectContractOutsourcingResponse>>> searchDailyReportDirectContractOutsourcings(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportDirectContractOutsourcingResponse> slice = dailyReportService
                .searchDailyReportDirectContractOutsourcings(
                        request,
                        PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                                sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 외주(공사) 조회")
    @GetMapping("/outsourcing-constructions")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportOutsourcingCompanyResponse>>> searchDailyReportOutsourcingConstructions(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportOutsourcingCompanyResponse> slice = dailyReportService
                .searchDailyReportOutsourcingConstructions(
                        request,
                        PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                                sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 외주(공사) 수정")
    @PatchMapping("/outsourcing-constructions")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportOutsourcingConstruction(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportOutsourcingConstructionUpdateRequest request) {
        dailyReportService.updateDailyReportOutsourcingConstructions(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 유류 조회")
    @GetMapping("/fuels")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportFuelResponse>>> searchDailyReportFuels(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportFuelResponse> slice = dailyReportService.searchDailyReportFuels(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 장비 조회")
    @GetMapping("/equipments")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportEquipmentResponse>>> searchDailyReportEquipments(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportEquipmentResponse> slice = dailyReportService.searchDailyReportEquipments(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 파일 조회")
    @GetMapping("/files")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportFileResponse>>> searchDailyReportFiles(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportFileResponse> slice = dailyReportService.searchDailyReportFiles(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 증빙 파일 조회")
    @GetMapping("/evidence-files")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportEvidenceFileResponse>>> searchDailyReportEvidenceFiles(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final Long id,
            @Valid final DailyReportEvidenceFileType fileType) {
        final Slice<DailyReportEvidenceFileResponse> slice = dailyReportService.searchDailyReportEvidenceFiles(
                id,
                fileType,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 마감")
    @PatchMapping("/complete")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.APPROVE)
    public ResponseEntity<Void> completeDailyReport(@Valid final DailyReportSearchRequest searchRequest) {
        dailyReportService.completeDailyReport(searchRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 작업 수정")
    @PatchMapping("/works")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportWork(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportWorkUpdateRequest request) {
        dailyReportService.updateDailyReportWork(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 주요공정 수정")
    @PatchMapping("/main-processes")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportMainProcess(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportMainProcessUpdateRequest request) {
        dailyReportService.updateDailyReportMainProcess(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 투입현황 수정")
    @PatchMapping("/input-statuses")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportInputStatus(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportInputStatusUpdateRequest request) {
        dailyReportService.updateDailyReportInputStatus(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 자재현황 수정")
    @PatchMapping("/material-statuses")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateDailyReportMaterialStatus(
            @Valid final DailyReportSearchRequest searchRequest,
            @Valid @RequestBody final DailyReportMaterialStatusUpdateRequest request) {
        dailyReportService.updateDailyReportMaterialStatus(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 작업 조회")
    @GetMapping("/works")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportWorkResponse>>> searchDailyReportWorks(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportWorkResponse> slice = dailyReportService.searchDailyReportWorks(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 주요공정 조회")
    @GetMapping("/main-processes")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportMainProcessResponse>>> searchDailyReportMainProcesses(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportMainProcessResponse> slice = dailyReportService.searchDailyReportMainProcesses(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 투입현황 조회")
    @GetMapping("/input-statuses")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportInputStatusResponse>>> searchDailyReportInputStatuses(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportInputStatusResponse> slice = dailyReportService.searchDailyReportInputStatuses(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 자재현황 조회")
    @GetMapping("/material-statuses")
    @RequireMenuPermission(menu = AppConstants.MENU_WORK_DAILY_REPORT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportMaterialStatusResponse>>> searchDailyReportMaterialStatuses(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final DailyReportSearchRequest request) {
        final Slice<DailyReportMaterialStatusResponse> slice = dailyReportService.searchDailyReportMaterialStatuses(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

}
