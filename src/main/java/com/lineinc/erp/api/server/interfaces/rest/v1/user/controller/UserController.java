package com.lineinc.erp.api.server.interfaces.rest.v1.user.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.BulkDeleteUsersRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.DownloadUserListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.SearchUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.UpdateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "유저 관리", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "비밀번호 초기화", description = "유저 로그인 비밀번호 초기화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping("/{id}/reset-password")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> resetPassword(@PathVariable final Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 목록 조회", description = "모든 유저 정보를 반환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<UserResponse>>> getAllUsers(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final SearchUserRequest request) {
        final Page<UserResponse> page = userService.getAllUsers(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "유저 키워드 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<UserResponse.UserSimpleResponse>>> searchUsersByName(
            @Valid final SortRequest sortRequest,
            @Valid final PageRequest pageRequest,
            @RequestParam(required = false) final String keyword,
            @RequestParam(required = false) final String loginIdKeyword,
            @RequestParam(required = false) final Boolean hasRole) {
        final Slice<UserResponse.UserSimpleResponse> slice = userService.searchUsers(keyword, loginIdKeyword,
                hasRole,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "유저 생성", description = "새로운 유저를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 생성 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createUser(@Valid @RequestBody final CreateUserRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        userService.createUser(request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 목록 엑셀 다운로드", description = "검색 조건에 맞는 유저 목록을 엑셀 파일로 다운로드합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public void downloadUserListExcel(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest,
            @Valid final SearchUserRequest request,
            @Valid final DownloadUserListRequest downloadUserListRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(downloadUserListRequest.fields());
        DownloadFieldUtils.validateFields(parsed, DownloadUserListRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "유저 목록.xlsx");

        try (Workbook workbook = userService.downloadExcel(
                user,
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
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
    public ResponseEntity<Void> deleteUsers(@RequestBody final BulkDeleteUsersRequest userIds) {
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
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateUserRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        userService.updateUser(id, request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 상세 조회", description = "유저 ID로 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<UserInfoResponse>> getUserDetail(@PathVariable final Long id) {
        final UserInfoResponse response = userService.getUserDetail(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @Operation(summary = "유저 변경 이력 조회", description = "특정 유저의 변경 히스토리를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_ACCOUNT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<UserChangeHistoryResponse>>> getUserChangeHistories(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Slice<UserChangeHistoryResponse> slice = userService.getUserChangeHistoriesSlice(
                id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()),
                user.getUserId());
        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
