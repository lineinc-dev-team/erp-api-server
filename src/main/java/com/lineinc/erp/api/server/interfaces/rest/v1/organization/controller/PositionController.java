package com.lineinc.erp.api.server.interfaces.rest.v1.organization.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.organization.service.v1.PositionService;
import com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response.PositionResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Tag(name = "직책 관리", description = "직책 관련 API")
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "직책 목록 조회", description = "모든 직책 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<PositionResponse>>> getAllPositions() {
        final List<PositionResponse> positions = positionService.getAllPositions();
        return ResponseEntity.ok(SuccessResponse.of(positions));
    }
}
