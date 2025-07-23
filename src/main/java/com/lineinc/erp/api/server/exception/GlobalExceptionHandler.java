package com.lineinc.erp.api.server.exception;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.response.ErrorResponse;
import com.lineinc.erp.api.server.common.response.FieldErrorDetail;
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
import org.springframework.web.HttpMediaTypeNotAcceptableException;
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
 * 컨트롤러 전역 예외 처리기
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ValidationMessages.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String message = error.getDefaultMessage();
                    if (message != null && message.contains("java.time.LocalDate")) {
                        message = ValidationMessages.INVALID_DATE_FORMAT;
                    }
                    return new FieldErrorDetail(error.getField(), message);
                }).toList();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        List<FieldErrorDetail> errors = new ArrayList<>();

        if (ex.getCause() instanceof InvalidFormatException ife) {
            ife.getPath().forEach(ref -> {
                String field = ref.getFieldName();
                String rejectedValue = String.valueOf(ife.getValue());
                String allowedValues = "";
                if (ife.getTargetType().isEnum()) {
                    allowedValues = " 허용된 값: " + Arrays.toString(ife.getTargetType().getEnumConstants());
                }
                errors.add(new FieldErrorDetail(field, rejectedValue + "." + allowedValues));
            });
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.DEFAULT_INVALID_INPUT, errors);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthentication(InternalAuthenticationServiceException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials() {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ValidationMessages.PASSWORD_MISMATCH);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        log.error("Unsupported Content-Type: {}", ex.getContentType(), ex);
        return buildErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ValidationMessages.UNSUPPORTED_CONTENT_TYPE + ": " + ex.getContentType()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        log.warn("HttpMediaTypeNotAcceptableException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, ValidationMessages.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IO Exception", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.FILE_PROCESS_ERROR);
    }

    @ExceptionHandler({PropertyReferenceException.class, InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorResponse> handleInvalidDataAccess(Exception ex) {
        log.warn("Invalid JPA property or data access: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.INVALID_PROPERTY_REFERENCE);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        log.warn("ResponseStatusException: {}", ex.getMessage());
        String message = ex.getReason() != null ? ex.getReason() : ValidationMessages.RESOURCE_NOT_FOUND;
        HttpStatus status;
        if (ex.getStatusCode() instanceof HttpStatus) {
            status = (HttpStatus) ex.getStatusCode();
        } else {
            status = HttpStatus.resolve(ex.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return buildErrorResponse(status, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return buildErrorResponse(status, message, List.of());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, List<FieldErrorDetail> errors) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), message, errors));
    }
}