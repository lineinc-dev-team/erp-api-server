package com.lineinc.erp.api.server.interfaces.rest.v1.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.request.LoginRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.request.PasswordChangeRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관리", description = "인증 관련 API")
public class AuthController extends BaseController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Operation(summary = "로그인", description = "사용자 로그인 후 세션 생성 및 쿠키 발급")
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody final LoginRequest request,
            final HttpServletRequest httpRequest,
            final HttpServletResponse response) {
        // 1. 로그인 인증 토큰 생성
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.loginId(),
                request.password());

        // 2. 실제 인증 수행
        final Authentication authentication = authenticationManager.authenticate(token);

        // 3. 로그인 성공한 사용자 정보
        final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.isActive()) {
            throw new IllegalStateException(ValidationMessages.USER_NOT_ACTIVE);
        }
        if (userDetails.isDeleted()) {
            throw new IllegalStateException(ValidationMessages.USER_NOT_FOUND);
        }

        // 4. 마지막 로그인 시간 갱신
        userService.updateLastLoginAt(userDetails.getUserId());

        // 5. SecurityContext에 인증 정보 저장
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 6. 세션에 SecurityContext 저장
        final HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context);

        // 세션 타임아웃 설정
        session.setMaxInactiveInterval(AppConstants.DEFAULT_SESSION_TIMEOUT_SECONDS);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "새 비밀번호로 변경합니다.")
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final PasswordChangeRequest request) {
        userService.changePassword(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "세션 만료를 통한 사용자 로그아웃 처리")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(final HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자 정보를 반환")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        final User user = userService.getUserEntity(userDetails.getUserId());
        final UserResponse response = userService.getUser(user.getId());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}