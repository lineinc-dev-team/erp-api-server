package com.lineinc.erp.api.server.exception;

import com.lineinc.erp.api.server.common.response.ErrorResponse;
import com.lineinc.erp.api.server.common.response.FieldErrorDetail;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 컨트롤러 전역에서 발생하는 예외를 처리하는 클래스
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_VALIDATION_ERROR_MESSAGE = "입력값이 유효하지 않습니다.";

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
                DEFAULT_VALIDATION_ERROR_MESSAGE,
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * JSON 파싱 실패 또는 Enum 값 불일치 시 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();

        // JSON 역직렬화 중 InvalidFormatException이 발생한 경우 (예: Enum 파싱 실패)
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            invalidFormatException.getPath().forEach(ref -> {
                String field = ref.getFieldName(); // 문제가 발생한 필드명
                String rejectedValue = String.valueOf(invalidFormatException.getValue()); // 잘못 전달된 값
                String allowedValues = "";

                // Enum 타입인 경우 허용된 값 목록을 문자열로 나열
                if (invalidFormatException.getTargetType().isEnum()) {
                    Object[] enumConstants = invalidFormatException.getTargetType().getEnumConstants();
                    allowedValues = " 허용된 값: " + Arrays.toString(enumConstants);
                }

                // 최종 오류 메시지 구성
                String message = rejectedValue + "'." + allowedValues;
                fieldErrors.add(new FieldErrorDetail(field, message));
            });
        }

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                DEFAULT_VALIDATION_ERROR_MESSAGE,
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
     * 지원하지 않는 Content-Type 요청 처리
     */
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
     * 접근 권한이 없는 경우 처리
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }

    /**
     * 입출력(IO) 예외 처리
     */
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