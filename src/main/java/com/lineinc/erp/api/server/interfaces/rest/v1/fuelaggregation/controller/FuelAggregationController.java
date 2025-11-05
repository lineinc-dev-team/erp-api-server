package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.fuelaggregation.service.v1.FuelAggregationService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.DeleteFuelAggregationsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelCompanyRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelPriceRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelCompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelPriceResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.WeatherTypeResponse;
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
@RequestMapping("/api/v1/fuel-aggregations")
@RequiredArgsConstructor
@Tag(name = "유류집계 관리")
public class FuelAggregationController extends BaseController {

    private final FuelAggregationService fuelAggregationService;

    @Operation(summary = "날씨 타입 조회")
    @GetMapping("/weather-types")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<WeatherTypeResponse>>> getWeatherTypes() {
        final List<WeatherTypeResponse> weatherTypes = Arrays.stream(FuelAggregationWeatherType.values())
                .map(WeatherTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(weatherTypes));
    }

    @Operation(summary = "유종 타입 조회")
    @GetMapping("/fuel-types")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<FuelTypeResponse>>> getFuelTypes() {
        final List<FuelTypeResponse> fuelTypes = Arrays.stream(FuelInfoFuelType.values())
                .map(FuelTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(fuelTypes));
    }

    @Operation(summary = "유류집계 등록")
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.CREATE)
    public ResponseEntity<SuccessResponse<Void>> createFuelAggregation(
            @Valid @RequestBody final FuelAggregationCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        fuelAggregationService.createFuelAggregation(request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유류집계 삭제")
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteFuelAggregations(
            @RequestBody final DeleteFuelAggregationsRequest request) {
        fuelAggregationService.deleteFuelAggregations(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유류집계 수정")
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateFuelAggregation(
            @PathVariable final Long id,
            @Valid @RequestBody final FuelAggregationUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        fuelAggregationService.updateFuelAggregation(id, request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유류집계 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<FuelAggregationListResponse>>> getFuelAggregations(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final FuelAggregationListRequest request) {

        final Page<FuelAggregationListResponse> page = fuelAggregationService.getAllFuelAggregations(
                user.getUserId(),
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "유류집계 상세 조회")
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<FuelAggregationDetailResponse>> getFuelAggregationById(
            @PathVariable final Long id) {
        final FuelAggregationDetailResponse response = fuelAggregationService.getFuelAggregationById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "유류집계 수정이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<FuelAggregationChangeHistoryResponse>>> getFuelAggregationChangeHistories(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Slice<FuelAggregationChangeHistoryResponse> slice = fuelAggregationService
                .getFuelAggregationChangeHistories(id,
                        PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()),
                        user.getUserId());

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "유종별 가격 조회")
    @GetMapping("/fuel-prices")
    public ResponseEntity<SuccessResponse<FuelPriceResponse>> getFuelPrice(
            @Valid final FuelPriceRequest request) {
        final FuelPriceResponse response = fuelAggregationService.getFuelPrice(request);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "유류업체 조회")
    @GetMapping("/fuel-company")
    public ResponseEntity<SuccessResponse<FuelCompanyResponse>> getFuelCompany(
            @Valid final FuelCompanyRequest request) {
        final FuelCompanyResponse response = fuelAggregationService.getFuelCompany(request);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "유류집계 목록 엑셀 다운로드")
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadFuelAggregationsExcel(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest,
            @Valid final FuelAggregationListRequest request,
            @Valid final FuelAggregationDownloadRequest downloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(downloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, FuelAggregationDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "유류집계 목록.xlsx");

        try (Workbook workbook = fuelAggregationService.downloadExcel(
                user,
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }
}
