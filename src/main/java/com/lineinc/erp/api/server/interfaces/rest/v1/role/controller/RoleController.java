package com.lineinc.erp.api.server.interfaces.rest.v1.role.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.role.service.v1.RoleService;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse.RoleSummaryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.CreateRolesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.DeleteRolesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UpdateRolesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.MenusPermissionsResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RolesResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
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

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "권한 그룹 관리", description = "권한 그룹 관련 API")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "권한 그룹 전체 조회", description = "등록된 모든 권한 그룹 정보를 반환합니다")
    @ApiResponse(responseCode = "200")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<RolesResponse>>> getAllRoles(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final UserWithRolesListRequest userWithRolesListRequest) {
        final Page<RolesResponse> page = roleService.getAllRoles(
                userWithRolesListRequest,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "권한 그룹명 키워드 검색", description = "권한 그룹명으로 키워드 검색을 수행합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<PagingResponse<RoleSummaryResponse>>> searchRolesByName(
            @Valid final SortRequest sortRequest,
            @Valid final PageRequest pageRequest,
            @RequestParam(required = false) final String keyword) {
        final Page<RoleSummaryResponse> page = roleService.searchRolesByName(keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "단일 권한 그룹 조회", description = "권한 그룹 ID로 단일 권한 그룹 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단일 권한 그룹 조회 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<RolesResponse>> getRoleById(@PathVariable final Long id) {
        final RolesResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "권한 그룹 메뉴별 권한 조회", description = "권한 그룹 ID로 해당 권한 그룹이 가지고 있는 메뉴별 권한 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴별 권한 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/menu-permissions")
    public ResponseEntity<SuccessResponse<List<MenusPermissionsResponse>>> getMenusPermissionsById(
            @PathVariable final Long id) {
        final List<MenusPermissionsResponse> responseList = roleService.getMenusPermissionsById(id);
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "권한 그룹에 속한 유저 목록 조회", description = "지정된 권한 그룹 ID에 속한 유저 목록을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 목록 조회 성공"),
    })
    @GetMapping("/{id}/users")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<RoleUserListResponse>>> getUsersByRoleId(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final RoleUserListRequest roleUserListRequest) {
        final Page<RoleUserListResponse> page = roleService.getUsersByRoleId(
                id,
                roleUserListRequest,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "권한 그룹 생성", description = "새로운 권한 그룹을 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400")
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createRole(
            @RequestBody @Valid final CreateRolesRequest request) {
        roleService.createRole(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "권한 그룹 수정", description = "권한 그룹 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateRole(
            @PathVariable final Long id,
            @RequestBody @Valid final UpdateRolesRequest request) {
        roleService.updateRole(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "여러 권한 그룹 삭제", description = "권한 그룹 ID 리스트로 여러 권한 그룹을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteRoles(@RequestBody final DeleteRolesRequest request) {
        roleService.deleteRolesByIds(request.roleIds());
        return ResponseEntity.ok().build();
    }
}
