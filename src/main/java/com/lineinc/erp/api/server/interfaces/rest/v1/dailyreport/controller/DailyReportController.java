package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.dailyreport.service.DailyReportService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/daily-reports")
@RequiredArgsConstructor
@Tag(name = "출역일보", description = "출역일보 관련 API")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    @Operation(summary = "출역일보 등록", description = "새로운 출역일보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출역일보 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장, 공정, 인력 등을 찾을 수 없음", content = @Content())
    })
    @PostMapping
    public ResponseEntity<Void> createDailyReport(
            @Valid @RequestBody DailyReportCreateRequest request) {
        dailyReportService.createDailyReport(request);
        return ResponseEntity.ok().build();
    }
}
