package com.lineinc.erp.api.server.presentation.v1.auth.controller;

import com.lineinc.erp.api.server.domain.user.service.UserService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.request.PasswordChangeRequest;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.request.LoginRequest;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Operation(summary = "로그인", description = "사용자 로그인 후 세션 생성 및 쿠키 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "401", description = "존재하지 않는 계정 또는 비밀번호 오류", content = @Content()),
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        // 1. 로그인 인증 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.loginId(), request.password());

        // 2. 실제 인증 수행
        Authentication authentication = authenticationManager.authenticate(token);

        // 3. 로그인 성공한 사용자 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.isActive()) {
            throw new IllegalStateException(ValidationMessages.USER_NOT_ACTIVE);
        }

        // 4. 마지막 로그인 시간 갱신
        userService.updateLastLoginAt(userDetails.getUserId());

        // 5. SecurityContext에 인증 정보 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 6. 세션에 SecurityContext 저장
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        // 세션 타임아웃 설정
        session.setMaxInactiveInterval(AppConstants.DEFAULT_SESSION_TIMEOUT_SECONDS);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "새 비밀번호로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        userService.changePassword(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "세션 만료를 통한 사용자 로그아웃 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content()),
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자 정보를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 반환"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.getUserEntity(userDetails.getUserId());
        UserResponse response = userService.getUser(user.getId());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}