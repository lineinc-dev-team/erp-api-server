package com.lineinc.erp.api.server.presentation.auth.controller;

import com.lineinc.erp.api.server.common.dto.SuccessResponse;
import com.lineinc.erp.api.server.domain.users.Users;
import com.lineinc.erp.api.server.domain.users.UsersRepository;
import com.lineinc.erp.api.server.presentation.auth.dto.LoginRequest;
import com.lineinc.erp.api.server.presentation.auth.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth") // 모든 엔드포인트 앞에 "/auth" 경로 접두어 설정
@RequiredArgsConstructor // final 필드에 대해 생성자 자동 주입
@Tag(name = "auth", description = "인증 관련 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;

    @Operation(summary = "로그인", description = "사용자 로그인 후 세션 생성 및 쿠키 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "401", description = "존재하지 않는 계정 또는 비밀번호 오류", content = @Content()),
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        // 1. 로그인 인증 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.loginId(), request.password());

        // 2. 실제 인증 수행
        Authentication authentication = authenticationManager.authenticate(token);

        // 3. 인증된 사용자 정보 SecurityContext에 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 4. 세션에 SecurityContext 저장
        httpRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

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
    public ResponseEntity<SuccessResponse<UserInfoResponse>> getCurrentUser(Authentication authentication) {
        Users user = usersRepository.findByLoginId(((Users) authentication.getPrincipal()).getLoginId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        UserInfoResponse response = new UserInfoResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getAccountType()
        );
        System.out.println(">> Response: " + response);


        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}