package com.lineinc.erp.api.server.presentation.v1.user.controller;

import com.lineinc.erp.api.server.application.user.UserService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UserListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "users", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "비밀번호 초기화", description = "유저 로그인 비밀번호 초기화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음", content = @Content())
    })
    @PostMapping("/{id}/reset-password")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모든 사용자 조회", description = "모든 유저 정보를 반환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<UserInfoResponse>>> getAllUsers(
            @Valid SortRequest sortRequest,
            @Valid PageRequest pageRequest,
            @Valid UserListRequest request
    ) {
        Page<UserInfoResponse> page = userService.getAllUsers(
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 생성 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }
}