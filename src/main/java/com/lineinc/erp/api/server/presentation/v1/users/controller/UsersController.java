package com.lineinc.erp.api.server.presentation.v1.users.controller;

import com.lineinc.erp.api.server.application.users.UsersService;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.UserInfoResponse;
import com.lineinc.erp.api.server.presentation.v1.users.dto.ResetPasswordRequest;
import com.lineinc.erp.api.server.presentation.v1.users.dto.UsersSearchRequest;
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
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "users", description = "유저 관련 API")
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "비밀번호 초기화", description = "유저 로그인 비밀번호 초기화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음", content = @Content())
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        usersService.resetPassword(request.loginId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모든 사용자 조회", description = "모든 유저 정보를 반환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<PagingResponse<UserInfoResponse>>> getAllUsers(
            @Valid UsersSearchRequest request
    ) {
        // 요청 DTO에서 페이지 번호와 페이지 크기 추출
        int page = request.page();
        int size = request.size();

        // 정렬 조건을 포함한 Pageable 객체 생성
        Pageable pageable = PageableUtils.createPageable(page, size, request.sort());

        // 사용자 목록 조회 (페이징 처리 포함)
        Page<UserInfoResponse> pageResult = usersService.getAllUsers(pageable);

        // 페이징 정보 구성
        PagingInfo pagingInfo = PagingInfo.from(pageResult);

        // 페이징 응답 형태로 변환
        PagingResponse<UserInfoResponse> response = new PagingResponse<>(pagingInfo, pageResult.getContent());

        // 성공 응답 반환
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}