package com.lineinc.erp.api.server.exception;

import com.lineinc.erp.api.server.common.response.ErrorResponse;
import com.lineinc.erp.api.server.common.response.FieldErrorDetail;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
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

    /**
     * 정적 리소스 요청 시 해당 리소스가 없을 때 발생하는 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                ValidationMessages.RESOURCE_NOT_FOUND,
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
                .map(error -> {
                    String field = error.getField();
                    String message = error.getDefaultMessage();

                    // 특정 필드에서 LocalDate 변환 실패시 메시지 가공
                    if (message != null && message.contains("java.time.LocalDate")) {
                        message = ValidationMessages.INVALID_DATE_FORMAT;
                    }

                    return new FieldErrorDetail(field, message);
                })
                .toList();

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ValidationMessages.DEFAULT_INVALID_INPUT,
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
                ValidationMessages.DEFAULT_INVALID_INPUT,
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
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ValidationMessages.PASSWORD_MISMATCH);
    }

    /**
     * 지원하지 않는 Content-Type 요청 처리
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.error("지원되지 않는 Content-Type 요청: {}", ex.getContentType(), ex);
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ValidationMessages.UNSUPPORTED_CONTENT_TYPE + ": " + ex.getContentType(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * 접근 권한이 없는 경우 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED);
    }

    /**
     * 입출력(IO) 예외 처리
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IO 예외 발생", ex);
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ValidationMessages.FILE_PROCESS_ERROR,
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 잘못된 속성 참조 예외 처리 (예: 잘못된 정렬 조건 등)
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReferenceException(PropertyReferenceException ex) {
        log.warn("잘못된 속성 참조: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ValidationMessages.INVALID_PROPERTY_REFERENCE,
                List.of()
        );
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 잘못된 JPA 접근 (예: 존재하지 않는 속성으로 정렬 등)
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex) {
        log.warn("잘못된 JPA 접근 오류: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ValidationMessages.INVALID_PROPERTY_REFERENCE,
                List.of()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("ResponseStatusException 발생: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ex.getStatusCode().value(),
                ex.getReason() != null ? ex.getReason() : ValidationMessages.RESOURCE_NOT_FOUND,
                List.of()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    /**
     * 잘못된 인자 (예: 정의되지 않은 필드 등) 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 요청 인자: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unhandled Exception", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR);
    }

    /**
     * 공통 ErrorResponse 생성 메서드
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.of(status.value(), message, List.of());
        return ResponseEntity.status(status).body(response);
    }
}