package com.lineinc.erp.api.server.presentation.v1.user.controller;

import com.lineinc.erp.api.server.application.user.UserService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.common.util.ResponseHeaderUtils;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "유저 관련 API")
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

    @Operation(summary = "모든 유저 조회", description = "모든 유저 정보를 반환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<UserResponse>>> getAllUsers(
            @Valid SortRequest sortRequest,
            @Valid PageRequest pageRequest,
            @Valid UserListRequest request
    ) {
        Page<UserResponse> page = userService.getAllUsers(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(summary = "유저 생성", description = "새로운 유저를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 생성 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "유저 목록 엑셀 다운로드", description = "검색 조건에 맞는 유저 목록을 엑셀 파일로 다운로드합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public void downloadUserListExcel(
            @Valid SortRequest sortRequest,
            @Valid UserListRequest request,
            @Valid UserDownloadRequest userDownloadRequest,
            HttpServletResponse response
    ) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(userDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, UserDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "유저 목록.xlsx");

        try (Workbook workbook = userService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed
        )) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "유저 계정 삭제", description = "선택한 유저 계정들을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "삭제 대상 유저를 찾을 수 없음", content = @Content())
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteUsers(@RequestBody DeleteUsersRequest userIds) {
        userService.deleteUsersByIds(userIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 정보 수정", description = "기존 유저 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "수정 대상 유저를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        userService.updateUser(id, request);
        return ResponseEntity.ok().build();
    }
}