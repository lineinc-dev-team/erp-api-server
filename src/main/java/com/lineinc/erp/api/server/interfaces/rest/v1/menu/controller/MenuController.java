package com.lineinc.erp.api.server.interfaces.rest.v1.menu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.menu.service.v1.MenuService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.menu.dto.response.MenuWithPermissionsResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "메뉴 관리")
public class MenuController extends BaseController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 및 권한 전체 조회")
    @GetMapping("/permissions")
    public ResponseEntity<SuccessResponse<List<MenuWithPermissionsResponse>>> getMenusWithPermissions() {
        final List<MenuWithPermissionsResponse> responseList = menuService.getMenusWithPermissions();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

}
