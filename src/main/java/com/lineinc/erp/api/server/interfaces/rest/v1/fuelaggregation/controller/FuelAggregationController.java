package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelType;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;
import com.lineinc.erp.api.server.domain.fuelaggregation.service.FuelAggregationService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.DeleteFuelAggregationsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.WeatherTypeResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.PageRequest;
import com.lineinc.erp.api.server.shared.dto.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fuel-aggregations")
@RequiredArgsConstructor
@Tag(name = "유류집계 관리", description = "유류집계 관리 API")
public class FuelAggregationController {

    private final FuelAggregationService fuelAggregationService;

    @Operation(summary = "날씨 타입 조회", description = "사용 가능한 날씨 타입 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/weather-types")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<WeatherTypeResponse>>> getWeatherTypes() {
        List<WeatherTypeResponse> weatherTypes = Arrays.stream(WeatherType.values())
                .map(WeatherTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(weatherTypes));
    }

    @Operation(summary = "유종 타입 조회", description = "사용 가능한 유종 타입 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/fuel-types")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<FuelTypeResponse>>> getFuelTypes() {
        List<FuelTypeResponse> fuelTypes = Arrays.stream(FuelType.values())
                .map(FuelTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(fuelTypes));
    }

    @Operation(summary = "유류집계 등록", description = "유류집계 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.CREATE)
    public ResponseEntity<SuccessResponse<Void>> createFuelAggregation(
            @Valid @RequestBody FuelAggregationCreateRequest request) {
        fuelAggregationService.createFuelAggregation(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유류집계 삭제", description = "하나 이상의 유류집계 ID를 받아 해당 유류집계를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "유류집계를 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteFuelAggregations(
            @RequestBody DeleteFuelAggregationsRequest request) {
        fuelAggregationService.deleteFuelAggregations(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유류집계 수정", description = "유류집계 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "유류집계를 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateFuelAggregation(
            @PathVariable Long id,
            @Valid @RequestBody FuelAggregationUpdateRequest request) {
        fuelAggregationService.updateFuelAggregation(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유류집계 목록 조회", description = "필터 조건에 맞는 유류집계 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유류집계 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/list")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<FuelAggregationListResponse>>> getFuelAggregations(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid FuelAggregationListRequest request) {

        Page<FuelAggregationListResponse> page = fuelAggregationService.getAllFuelAggregations(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "유류집계 상세 조회", description = "유류집계 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유류집계 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유류집계를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<FuelAggregationDetailResponse>> getFuelAggregationById(
            @PathVariable Long id) {
        FuelAggregationDetailResponse response = fuelAggregationService.getFuelAggregationById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "유류집계 수정이력 조회", description = "유류집계의 수정이력을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유류집계를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<FuelAggregationChangeHistoryResponse>>> getFuelAggregationChangeHistories(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {

        Slice<FuelAggregationChangeHistoryResponse> slice = fuelAggregationService.getFuelAggregationChangeHistories(id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "유류집계 목록 엑셀 다운로드", description = "검색 조건에 맞는 유류집계 목록을 엑셀 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public void downloadFuelAggregationsExcel(
            @Valid SortRequest sortRequest,
            @Valid FuelAggregationListRequest request,
            @Valid FuelAggregationDownloadRequest downloadRequest,
            HttpServletResponse response) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(downloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, FuelAggregationDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "유류집계 목록.xlsx");

        try (Workbook workbook = fuelAggregationService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }
}
