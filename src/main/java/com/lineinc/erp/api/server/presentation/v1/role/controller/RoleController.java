package com.lineinc.erp.api.server.presentation.v1.role.controller;

import com.lineinc.erp.api.server.application.role.RoleService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.AddUsersToRoleRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.CreateRolesRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.RemoveUsersFromRoleRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.MenusPermissionsResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RoleUserListResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "roles", description = "권한 그룹 관련 API")
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "권한 그룹 전체 조회",
            description = "등록된 모든 권한 그룹 정보를 반환합니다"
    )
    @ApiResponse(responseCode = "200", description = "권한 그룹 목록 조회 성공")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<RolesResponse>>> getAllRoles(
            @Valid PageRequest pageRequest,
            @Valid UserWithRolesListRequest userWithRolesListRequest
    ) {
        Page<RolesResponse> page = roleService.getAllRoles(
                userWithRolesListRequest,
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
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
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
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<MenusPermissionsResponse>>> getMenusPermissionsById(@PathVariable Long id) {
        List<MenusPermissionsResponse> responseList = roleService.getMenusPermissionsById(id);
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(
            summary = "권한 그룹에 속한 유저 목록 조회",
            description = "지정된 권한 그룹 ID에 속한 유저 목록을 반환합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 목록 조회 성공"),
    })
    @GetMapping("/{id}/users")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<RoleUserListResponse>>> getUsersByRoleId(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid RoleUserListRequest roleUserListRequest
    ) {
        Page<RoleUserListResponse> page = roleService.getUsersByRoleId(
                id,
                roleUserListRequest,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size())
        );

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(
            summary = "권한 그룹에서 유저 삭제",
            description = "권한 그룹 ID와 유저 ID로 해당 권한 그룹에서 유저를 삭제합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @DeleteMapping("/{id}/users")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.DELETE)
    public ResponseEntity<Void> removeUsersFromRole(
            @PathVariable Long id,
            @RequestBody RemoveUsersFromRoleRequest request
    ) {
        roleService.removeUsersFromRole(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "권한 그룹에 유저 추가",
            description = "권한 그룹 ID와 유저 ID 리스트로 해당 권한 그룹에 유저를 추가합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @PostMapping("/{id}/users")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.CREATE)
    public ResponseEntity<Void> addUsersToRole(
            @PathVariable Long id,
            @RequestBody AddUsersToRoleRequest request
    ) {
        roleService.addUsersToRole(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "권한 그룹 삭제",
            description = "권한 그룹 ID로 권한 그룹을 삭제합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "권한 그룹을 찾을 수 없음", content = @Content())
    })
    @DeleteMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteRoleById(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "권한 그룹 생성",
            description = "새로운 권한 그룹을 생성합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content())
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_PERMISSION, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createRole(
            @RequestBody @Valid CreateRolesRequest request
    ) {
        roleService.createRole(request);
        return ResponseEntity.ok().build();
    }

}
