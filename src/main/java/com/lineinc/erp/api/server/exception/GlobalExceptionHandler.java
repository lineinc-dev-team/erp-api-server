package com.lineinc.erp.api.server.exception;

import com.lineinc.erp.api.server.common.response.ErrorResponse;
import com.lineinc.erp.api.server.common.response.FieldErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;

/**
 * 컨트롤러 전역에서 발생하는 예외를 처리하는 클래스
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 정적 리소스 요청 시 해당 리소스가 없을 때 발생하는 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "요청한 리소스를 찾을 수 없습니다.",
                List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 유효성 검사 실패 시 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "입력값이 유효하지 않습니다.",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 존재하지 않는 사용자명 예외 처리
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthenticationException(InternalAuthenticationServiceException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * 비밀번호 불일치 예외 처리
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials() {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
    }

    /**
     * 명시적으로 상태 코드가 설정된 예외
     */
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex) {
        ErrorResponse response = ErrorResponse.of(
                ex.getStatusCode().value(),
                ex.getReason() != null ? ex.getReason() : "요청 처리 중 오류가 발생했습니다.",
                List.of()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    /**
     * 접근 권한이 없는 경우
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IO 예외 발생", ex);
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "파일 처리 중 오류가 발생했습니다.",
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.error("지원되지 않는 Content-Type 요청: {}", ex.getContentType(), ex);
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "지원하지 않는 콘텐츠 타입입니다: " + ex.getContentType(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unhandled Exception", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }

    /**
     * 공통 ErrorResponse 생성 메서드
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.of(status.value(), message, List.of());
        return ResponseEntity.status(status).body(response);
    }
}