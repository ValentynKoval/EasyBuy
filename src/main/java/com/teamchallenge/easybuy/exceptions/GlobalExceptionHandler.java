package com.teamchallenge.easybuy.exceptions;

import com.teamchallenge.easybuy.exceptions.goods.CategoryAttributeException;
import com.teamchallenge.easybuy.exceptions.goods.CategoryNotFoundException;
import com.teamchallenge.easybuy.exceptions.goods.GoodsAttributeValueException;
import com.teamchallenge.easybuy.exceptions.goods.GoodsImageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for centralized error handling.
 * This handler catches specific exceptions and returns a JSON response with appropriate HTTP status codes.
 * The response includes timestamp, status, error message, and the request path for better debugging.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles CategoryNotFoundException with HTTP 404 status.
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFoundException(CategoryNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles GoodsImageException with HTTP 404 status.
     */
    @ExceptionHandler(GoodsImageException.class)
    public ResponseEntity<Map<String, Object>> handleGoodsImageException(GoodsImageException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles GoodsAttributeValueException with HTTP 404 status.
     */
    @ExceptionHandler(GoodsAttributeValueException.class)
    public ResponseEntity<Map<String, Object>> handleGoodsAttributeValueException(GoodsAttributeValueException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles CategoryAttributeException with HTTP 404 status.
     */
    @ExceptionHandler(CategoryAttributeException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryAttributeException(CategoryAttributeException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles validation errors (e.g., from @Valid) with HTTP 400 status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions with HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex, WebRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred: " + ex.getClass().getSimpleName();
        return buildErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Builds a standardized error response map.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, status);
    }
}