package com.lawauto.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed for request [{}]: {}", request.getRequestURI(), ex.getMessage());
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a, b) -> a
                ));

        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(400, "Validation failed", request.getRequestURI(), fieldErrors)
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        log.warn("Bad request [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "Bad request", request.getRequestURI()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing request parameter [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "Bad request", request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("Route not found [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(404, "Not found", request.getRequestURI()));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(403, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(org.springframework.web.server.ResponseStatusException ex, HttpServletRequest request) {
        log.warn("Response status exception [{}]: {} - {}", request.getRequestURI(), ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiErrorResponse.of(ex.getStatusCode().value(), ex.getReason(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred while processing request [{}]", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(500, "Unexpected error", request.getRequestURI()));
    }
}
