package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.weather.service.WeatherAggregationService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.DailyWeatherAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DailyWeatherAggregationResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/aggregation/daily-weather")
@Tag(name = "집계")
public class DailyWeatherAggregationController extends BaseController {

    private final WeatherAggregationService weatherAggregationService;

    @GetMapping
    @Operation(summary = "월별 일자별 날씨 조회")
    public ResponseEntity<SuccessResponse<DailyWeatherAggregationResponse>> getDailyWeather(
            @Valid final DailyWeatherAggregationRequest request) {
        final DailyWeatherAggregationResponse response = weatherAggregationService.getDailyWeather(request);
        return SuccessResponse.ok(response);
    }
}
