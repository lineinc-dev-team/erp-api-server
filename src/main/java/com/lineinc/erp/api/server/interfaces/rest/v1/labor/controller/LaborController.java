package com.lineinc.erp.api.server.interfaces.rest.v1.labor.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.labor.enums.LaborWorkType;
import com.lineinc.erp.api.server.domain.labor.service.v1.LaborService;
import com.lineinc.erp.api.server.domain.laborpayroll.service.v1.LaborPayrollService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.DeleteLaborsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.ResidentNumberDuplicateResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.TypeDescriptionResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.WorkTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollHistoryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/labors")
@RequiredArgsConstructor
@Tag(name = "노무 관리")
public class LaborController extends BaseController {

    private final LaborService laborService;
    private final LaborPayrollService laborPayrollService;

    @Operation(summary = "노무 인력정보 등록")
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createLabor(@Valid @RequestBody final LaborCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        laborService.createLabor(request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "노무 구분 조회")
    @GetMapping("/labor-types")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<LaborTypeResponse>>> getLaborTypes() {
        final List<LaborTypeResponse> laborTypes =
                Arrays.stream(LaborType.values()).map(LaborTypeResponse::from).toList();
        return ResponseEntity.ok(SuccessResponse.of(laborTypes));
    }

    @Operation(summary = "공종 구분 조회")
    @GetMapping("/work-types")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<WorkTypeResponse>>> getWorkTypes(
            @RequestParam(required = false) final String keyword) {
        final List<WorkTypeResponse> workTypes = Arrays.stream(LaborWorkType.values())
                .filter(workType -> keyword == null || keyword.isBlank() || workType.getLabel().contains(keyword))
                .sorted(Comparator.comparingInt(LaborWorkType::getOrder)).map(WorkTypeResponse::from).toList();
        return ResponseEntity.ok(SuccessResponse.of(workTypes));
    }

    @Operation(summary = "인력정보 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<LaborListResponse>>> getLaborList(
            @ModelAttribute final LaborListRequest request, @ModelAttribute final PageRequest pageRequest,
            @ModelAttribute final SortRequest sortRequest) {
        final Page<LaborListResponse> page = laborService.getLaborList(request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "ETC 노무 구분 설명 키워드 검색")
    @GetMapping("/etc-type-descriptions/search")
    public ResponseEntity<SuccessResponse<SliceResponse<TypeDescriptionResponse>>> searchEtcTypeDescriptions(
            @RequestParam(required = false) final String keyword, @ModelAttribute final PageRequest pageRequest) {
        final Slice<TypeDescriptionResponse> slice = laborService.getEtcTypeDescriptions(keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size()));

        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "인력정보 삭제")
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteLabors(@Valid @RequestBody final DeleteLaborsRequest request) {
        laborService.deleteLaborsByIds(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인력정보 목록 엑셀 다운로드")
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadLaborsExcel(@AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest, @Valid final LaborListRequest request,
            @Valid final LaborDownloadRequest laborDownloadRequest, final HttpServletResponse response)
            throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(laborDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, LaborDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "인력정보 목록.xlsx");

        try (Workbook workbook =
                laborService.downloadExcel(user, request, PageableUtils.parseSort(sortRequest.sort()), parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "인력정보 상세 조회")
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<LaborDetailResponse>> getLaborDetail(@PathVariable final Long id) {
        final LaborDetailResponse laborResponse = laborService.getLaborById(id);
        return ResponseEntity.ok(SuccessResponse.of(laborResponse));
    }

    @Operation(summary = "인력정보 수정")
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateLabor(@PathVariable final Long id,
            @Valid @RequestBody final LaborUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        laborService.updateLabor(id, request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인력정보 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<LaborChangeHistoryResponse>>> getLaborChangeHistories(
            @PathVariable final Long id, @Valid final PageRequest pageRequest, @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Slice<LaborChangeHistoryResponse> slice = laborService.getLaborChangeHistories(id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()),
                user.getUserId());
        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "인력명 키워드 검색")
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<LaborNameResponse>>> getLaborNames(
            @Valid final PageRequest pageRequest, @Valid final SortRequest sortRequest,
            @RequestParam(required = false) final String keyword,
            @RequestParam(required = false) final List<LaborType> types,
            @RequestParam(required = false) final Long outsourcingCompanyId,
            @RequestParam(required = false) final Long outsourcingCompanyContractId,
            @RequestParam(required = false) final Boolean isHeadOffice) {
        final Pageable pageable =
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort());
        final Slice<LaborNameResponse> slice = laborService.getLaborNames(keyword, types, outsourcingCompanyId,
                outsourcingCompanyContractId, isHeadOffice, pageable);

        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "노무인력 명세서 이력 조회")
    @GetMapping("/{id}/payrolls")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<LaborPayrollHistoryResponse>>> getLaborPayrolls(
            @PathVariable final Long id, @ModelAttribute final PageRequest pageRequest) {

        final Page<LaborPayrollHistoryResponse> page = laborPayrollService.getLaborPayrollsByLaborId(id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size()));

        return ResponseEntity.ok(SuccessResponse.of(new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "주민등록번호 중복 검사")
    @GetMapping("/check-resident-number-duplicate")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<ResidentNumberDuplicateResponse>> checkResidentNumberDuplicate(
            @RequestParam final String residentNumber) {
        final boolean isDuplicate = laborService.checkResidentNumberDuplicate(residentNumber);
        return ResponseEntity.ok(SuccessResponse.of(new ResidentNumberDuplicateResponse(isDuplicate)));
    }
}
