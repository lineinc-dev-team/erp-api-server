package com.lineinc.erp.api.server.interfaces.rest.v1.organization.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.organization.service.v1.DepartmentService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response.DepartmentResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "부서 관리", description = "부서 관련 API")
public class DepartmentController extends BaseController {

    private final DepartmentService departmentService;

    @Operation(summary = "부서 목록 조회", description = "모든 부서 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<SuccessResponse<List<DepartmentResponse>>> getAllDepartments() {
        final List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(SuccessResponse.of(departments));
    }

}
