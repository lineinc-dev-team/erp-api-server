package com.lineinc.erp.api.server.interfaces.rest.v1.organization.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.organization.service.v1.GradeService;
import com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response.GradeResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "직급 관리", description = "직급 관련 API")
public class GradeController {

    private final GradeService gradeService;

    @Operation(summary = "직급 목록 조회", description = "모든 직급 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<GradeResponse>>> getAllGrades() {
        final List<GradeResponse> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(SuccessResponse.of(grades));
    }

}
