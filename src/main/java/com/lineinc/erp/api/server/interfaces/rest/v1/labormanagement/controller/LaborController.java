package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;
import com.lineinc.erp.api.server.domain.labormanagement.service.LaborService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.WorkTypeResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.PageRequest;
import com.lineinc.erp.api.server.shared.dto.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.TypeDescriptionResponse;
import org.springframework.web.bind.annotation.RequestParam;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import org.springframework.data.domain.Slice;

@RestController
@RequestMapping("/api/v1/labors")
@RequiredArgsConstructor
@Tag(name = "노무 관리", description = "노무 관련 API")
public class LaborController {

    private final LaborService laborService;

    @Operation(summary = "노무 인력정보 등록", description = "노무 인력정보를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "노무 등록 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createLabor(@Valid @RequestBody LaborCreateRequest request) {
        laborService.createLabor(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "노무 구분 조회", description = "사용 가능한 노무 구분 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/labor-types")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<LaborTypeResponse>>> getLaborTypes() {
        List<LaborTypeResponse> laborTypes = Arrays.stream(LaborType.values())
                .map(LaborTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(laborTypes));
    }

    @Operation(summary = "공종 구분 조회", description = "사용 가능한 공종 구분 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/work-types")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<WorkTypeResponse>>> getWorkTypes() {
        List<WorkTypeResponse> workTypes = Arrays.stream(WorkType.values())
                .map(WorkTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(workTypes));
    }

    @Operation(summary = "인력정보 목록 조회", description = "조건에 따른 인력정보 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<LaborListResponse>>> getLaborList(
            @ModelAttribute LaborListRequest request,
            @ModelAttribute PageRequest pageRequest,
            @ModelAttribute SortRequest sortRequest) {
        Page<LaborListResponse> page = laborService.getLaborList(request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "ETC 노무 구분 설명 키워드 검색", description = "type이 ETC인 모든 노무의 구분 설명을 키워드로 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/etc-type-descriptions/search")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<TypeDescriptionResponse>>> searchEtcTypeDescriptions(
            @RequestParam(required = false) String keyword,
            @ModelAttribute PageRequest pageRequest,
            @ModelAttribute SortRequest sortRequest) {
        Slice<TypeDescriptionResponse> slice = laborService.getEtcTypeDescriptions(
                keyword, PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
