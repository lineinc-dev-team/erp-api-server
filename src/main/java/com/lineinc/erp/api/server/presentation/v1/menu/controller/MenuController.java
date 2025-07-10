package com.lineinc.erp.api.server.presentation.v1.menu.controller;

import com.lineinc.erp.api.server.application.menu.MenuService;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.presentation.v1.menu.dto.response.MenuWithPermissionsResponse;
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
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "menus", description = "메뉴 및 권한 관련 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(
            summary = "메뉴 및 권한 전체 조회",
            description = "등록된 모든 메뉴와 각 메뉴에 대한 권한 정보를 반환합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 및 권한 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @GetMapping("/menus-with-permissions")
    public ResponseEntity<SuccessResponse<List<MenuWithPermissionsResponse>>> getMenusWithPermissions() {
        List<MenuWithPermissionsResponse> responseList = menuService.getMenusWithPermissions();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

}
