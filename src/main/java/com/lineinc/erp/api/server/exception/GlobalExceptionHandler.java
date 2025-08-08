package com.lineinc.erp.api.server.exception;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.response.ErrorResponse;
import com.lineinc.erp.api.server.common.response.FieldErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestControllerAdvice // 전역적으로 모든 @RestController 에서 발생한 예외를 처리해주는 클래스
public class GlobalExceptionHandler {

    // 1. 인증/인가 관련
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("BadCredentialsException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ValidationMessages.PASSWORD_MISMATCH, List.of());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthentication(InternalAuthenticationServiceException ex) {
        log.warn("InternalAuthenticationServiceException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("AccessDeniedException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED, List.of());
    }

    // 2. 유효성/요청 관련
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException: {}", ex.getMessage(), ex);
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField()))
                .toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
        if (ex.getCause() instanceof InvalidFormatException ife && !ife.getPath().isEmpty()) {
            List<FieldErrorDetail> errors = ife.getPath().stream()
                    .map(ref -> new FieldErrorDetail(ref.getFieldName()))
                    .toList();
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, errors);
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.warn("IllegalStateException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), List.of());
    }

    // 3. 미디어 타입 관련
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        log.warn("UnsupportedMediaTypeException: {} - {}", ex.getContentType(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ValidationMessages.UNSUPPORTED_CONTENT_TYPE + ": " + ex.getContentType(), List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        log.warn("HttpMediaTypeNotAcceptableException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, ValidationMessages.NOT_ACCEPTABLE, List.of());
    }

    // 4. 자원/경로 관련
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("NoResourceFoundException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, ValidationMessages.RESOURCE_NOT_FOUND, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        log.warn("ResponseStatusException: {}", ex.getMessage(), ex);
        var message = ex.getReason() != null ? ex.getReason() : ValidationMessages.RESOURCE_NOT_FOUND;
        var status = HttpStatus.resolve(ex.getStatusCode().value());
        return buildErrorResponse(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, message, List.of());
    }

    // 5. 데이터 액세스 관련
    @ExceptionHandler({PropertyReferenceException.class, InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorResponse> handleInvalidDataAccess(Exception ex) {
        log.warn("InvalidDataAccessException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.INVALID_PROPERTY_REFERENCE, List.of());
    }

    // 6. IO 및 기타
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IOException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.FILE_PROCESS_ERROR, List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR, List.of());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, List<FieldErrorDetail> errors) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), message, errors));
    }
}
