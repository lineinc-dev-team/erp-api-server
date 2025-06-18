package com.lineinc.erp.api.server.interfaces.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // 모든 엔드포인트 앞에 "/auth" 경로 접두어 설정
@RequiredArgsConstructor // final 필드에 대해 생성자 자동 주입
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        // 클라이언트로부터 받은 로그인 ID와 비밀번호로 인증 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword());

        // AuthenticationManager를 사용해 인증 처리 (비밀번호 검증 등)
        Authentication authentication = authenticationManager.authenticate(token);

        // 인증 성공 시, SecurityContext에 인증 정보 저장 → 세션에 자동 저장됨 (세션 기반 인증)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 로그인 성공 응답 반환
        return ResponseEntity.ok("✅ 로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("👋 로그아웃 성공");
    }
}