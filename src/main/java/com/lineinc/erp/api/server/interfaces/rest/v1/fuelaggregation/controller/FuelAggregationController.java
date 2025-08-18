package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;
import com.lineinc.erp.api.server.domain.fuelaggregation.service.FuelAggregationService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.WeatherTypeResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    // @Operation(summary = "유류집계 등록", description = "유류집계 정보를 등록합니다.")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "성공", content = @Content()),
    // @ApiResponse(responseCode = "400", description = "입력값 오류", content =
    // @Content())
    // })
    // @PostMapping
    // @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action =
    // PermissionAction.CREATE)
    // public ResponseEntity<SuccessResponse<Void>> createFuelAggregation(
    // @Valid @RequestBody FuelAggregationCreateRequest request) {
    // fuelAggregationService.createFuelAggregation(request);
    // return ResponseEntity.ok().build();
    // }
}
