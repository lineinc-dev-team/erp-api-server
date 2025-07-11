package com.lineinc.erp.api.server.presentation.v1.role.controller;

import com.lineinc.erp.api.server.application.role.RoleService;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.MenusPermissionsResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RolesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "roles", description = "권한 그룹 관련 API")
public class RoleController {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @Operation(
            summary = "권한 그룹 전체 조회",
            description = "등록된 모든 권한 그룹 정보를 반환합니다"
    )
    @ApiResponse(responseCode = "200", description = "권한 그룹 목록 조회 성공")
    @GetMapping
    public ResponseEntity<SuccessResponse<PagingResponse<RolesResponse>>> getAllRoles(
            @Valid PageRequest pageRequest
    ) {
        Page<RolesResponse> page = roleService.getAllRoles(
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size())
        );

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(
            summary = "단일 권한 그룹 조회",
            description = "권한 그룹 ID로 단일 권한 그룹 정보를 조회합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단일 권한 그룹 조회 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<RolesResponse>> getRoleById(@PathVariable Long id) {
        RolesResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(
            summary = "권한 그룹 메뉴별 권한 조회",
            description = "권한 그룹 ID로 해당 권한 그룹이 가지고 있는 메뉴별 권한 정보를 조회합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴별 권한 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/menu-permissions")
    public ResponseEntity<SuccessResponse<List<MenusPermissionsResponse>>> getMenusPermissionsById(@PathVariable Long id) {
        List<MenusPermissionsResponse> responseList = roleService.getMenusPermissionsById(id);
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

}
