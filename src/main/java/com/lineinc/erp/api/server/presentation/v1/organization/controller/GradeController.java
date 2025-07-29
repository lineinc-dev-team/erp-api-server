package com.lineinc.erp.api.server.presentation.v1.organization.controller;

import com.lineinc.erp.api.server.application.organization.GradeService;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.presentation.v1.organization.dto.response.GradeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "직급 관련 API")
public class GradeController {

    private final GradeService gradeService;

    @Operation(summary = "직급 목록 조회", description = "모든 직급 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<GradeResponse>>> getAllGrades() {
        List<GradeResponse> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(SuccessResponse.of(grades));
    }

}
