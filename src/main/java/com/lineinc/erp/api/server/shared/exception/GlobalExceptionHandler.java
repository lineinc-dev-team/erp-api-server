package com.lineinc.erp.api.server.shared.exception;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lineinc.erp.api.server.shared.dto.response.ErrorResponse;
import com.lineinc.erp.api.server.shared.dto.response.FieldErrorDetail;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.extern.slf4j.Slf4j;

/**
 * 전역적으로 모든 {@code @RestController} 에서 발생한 예외를 처리해주는 클래스입니다.
 * 단, 인증 실패(401)는 Spring Security의 AuthenticationEntryPoint에서 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(final BadCredentialsException ex) {
        log.warn("로그인 실패: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ValidationMessages.PASSWORD_MISMATCH, List.of());
    }

    // 1. 인증/인가 관련
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthentication(final InternalAuthenticationServiceException ex) {
        log.warn("InternalAuthenticationServiceException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(final AccessDeniedException ex) {
        log.warn("AccessDeniedException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED, List.of());
    }

    // 2. 유효성/요청 관련
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException: {}", ex.getMessage(), ex);
        final List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex) {
        log.warn("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
        if (ex.getCause() instanceof final InvalidFormatException ife && !ife.getPath().isEmpty()) {
            // 경로를 하나의 필드명으로 결합 (예: details[0].category)
            final String fieldName = ife.getPath().stream()
                    .map(ref -> {
                        if (ref.getIndex() >= 0) {
                            return String.format("%s[%d]", ref.getFieldName(), ref.getIndex());
                        }
                        return ref.getFieldName();
                    })
                    .filter(name -> name != null && !"null".equals(name))
                    .collect(Collectors.joining("."));

            String message = null;
            // Enum 타입 에러인 경우 사용 가능한 값들을 포함한 메시지 생성
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                final Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) ife.getTargetType();
                final String[] enumValues = Arrays.stream(enumClass.getEnumConstants())
                        .map(Enum::name)
                        .toArray(String[]::new);
                message = String.format("사용 가능한 값: %s", String.join(", ", enumValues));
            }

            final List<FieldErrorDetail> errors = List.of(new FieldErrorDetail(fieldName, message));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, errors);
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(final IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(final IllegalStateException ex) {
        log.warn("IllegalStateException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            final MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException: {} - {}", ex.getName(), ex.getValue(), ex);

        String message;
        if (ex.getValue() != null && "NaN".equals(ex.getValue().toString())) {
            message = ValidationMessages.DEFAULT_INVALID_INPUT;
        } else {
            message = String.format("파라미터 '%s'의 값 '%s'을(를) 올바른 형식으로 변환할 수 없습니다.",
                    ex.getName(), ex.getValue());
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, List.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex) {
        log.warn("MissingServletRequestParameterException: {} - {}", ex.getParameterName(), ex.getParameterType(), ex);
        final String message = String.format("필수 파라미터 '%s'가 제공되지 않았습니다.", ex.getParameterName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, List.of());
    }

    // 3. 미디어 타입 관련
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(final HttpMediaTypeNotSupportedException ex) {
        log.warn("UnsupportedMediaTypeException: {} - {}", ex.getContentType(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ValidationMessages.UNSUPPORTED_CONTENT_TYPE + ": " + ex.getContentType(), List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(
            final HttpMediaTypeNotAcceptableException ex) {
        log.warn("HttpMediaTypeNotAcceptableException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, ValidationMessages.NOT_ACCEPTABLE, List.of());
    }

    // 4. HTTP 메소드 관련
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
        log.warn("HttpRequestMethodNotSupportedException: {} 메소드는 지원되지 않습니다. 지원되는 메소드: {}",
                ex.getMethod(), ex.getSupportedMethods(), ex);

        final String supportedMethods = ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods())
                : "확인 필요";
        final String message = String.format("'%s' 메소드는 지원되지 않습니다. 지원되는 메소드: %s",
                ex.getMethod(), supportedMethods);

        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message, List.of());
    }

    // 5. 자원/경로 관련
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(final NoResourceFoundException ex) {
        log.warn("NoResourceFoundException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, ValidationMessages.RESOURCE_NOT_FOUND, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(final ResponseStatusException ex) {
        log.warn("ResponseStatusException: {}", ex.getMessage(), ex);
        final var message = ex.getReason() != null ? ex.getReason() : ValidationMessages.RESOURCE_NOT_FOUND;
        final var status = HttpStatus.resolve(ex.getStatusCode().value());
        return buildErrorResponse(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, message, List.of());
    }

    // 6. 데이터 액세스 관련
    @ExceptionHandler({ PropertyReferenceException.class, InvalidDataAccessApiUsageException.class })
    public ResponseEntity<ErrorResponse> handleInvalidDataAccess(final Exception ex) {
        log.warn("InvalidDataAccessException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.INVALID_PROPERTY_REFERENCE, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(final DataIntegrityViolationException ex) {
        log.warn("DataIntegrityViolationException: {}", ex.getMessage(), ex);

        final String message = ex.getMessage();
        String userFriendlyMessage;

        // 외래키 제약조건 위반인 경우
        if (message != null && message.contains("violates foreign key constraint")) {
            if (message.contains("Key (department_id)=")) {
                userFriendlyMessage = ValidationMessages.DEPARTMENT_NOT_FOUND;
            } else if (message.contains("Key (grade_id)=")) {
                userFriendlyMessage = ValidationMessages.GRADE_NOT_FOUND;
            } else if (message.contains("Key (position_id)=")) {
                userFriendlyMessage = ValidationMessages.POSITION_NOT_FOUND;
            } else {
                userFriendlyMessage = ValidationMessages.DEFAULT_INVALID_INPUT;
            }
        } else {
            userFriendlyMessage = ValidationMessages.DEFAULT_INVALID_INPUT;
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, userFriendlyMessage, List.of());
    }

    // 7. IO 및 기타
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(final IOException ex) {
        log.error("IOException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.FILE_PROCESS_ERROR, List.of());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(final NullPointerException ex) {
        log.error("NullPointerException: {}", ex.getMessage(), ex);

        // 에러 메시지에서 원인 파악
        String message = ValidationMessages.DEFAULT_INVALID_INPUT;
        if (ex.getMessage() != null) {
            // 파라미터가 null인 경우 구체적인 메시지 생성
            if (ex.getMessage().contains("because \"") && ex.getMessage().contains("\" is null")) {
                final String nullField = extractNullFieldName(ex.getMessage());
                if (nullField != null) {
                    message = String.format("필수 파라미터 '%s'가 제공되지 않았습니다.", nullField);
                }
            } else if (ex.getMessage().contains("because the return value of")) {
                message = ValidationMessages.DEFAULT_INVALID_INPUT;
            }
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, List.of());
    }

    /**
     * NullPointerException 메시지에서 null인 필드명 추출
     * 예: "Cannot invoke \"com.example.BatchName.getLabel()\" because \"batchName\"
     * is null"
     * -> "batchName" 반환
     */
    private String extractNullFieldName(final String message) {
        if (message == null) {
            return null;
        }
        final int becauseIndex = message.indexOf("because \"");
        if (becauseIndex == -1) {
            return null;
        }
        final int startIndex = becauseIndex + 9; // "because \"" 길이
        final int endIndex = message.indexOf("\" is null", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return message.substring(startIndex, endIndex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR,
                List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(final Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR,
                List.of());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(final HttpStatus status, final String message,
            final List<FieldErrorDetail> errors) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), message, errors));
    }
}
