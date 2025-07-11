package com.lineinc.erp.api.server.presentation.v1.role.controller;

import com.lineinc.erp.api.server.application.role.RoleService;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.presentation.v1.menu.dto.response.MenuWithPermissionsResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RoleWithMenusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
            summary = "권한 그룹에 해당하는 메뉴 및 권한 조회",
            description = "특정 권한 그룹 ID에 연결된 메뉴 및 권한 정보를 반환합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "권한 그룹별 메뉴 및 권한 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "권한그룹을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{roleId}/menus")
    public ResponseEntity<SuccessResponse<RoleWithMenusResponse>> getRoleWithMenus(@PathVariable Long roleId) {
        RoleWithMenusResponse response = roleService.getRoleWithMenus(roleId);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
