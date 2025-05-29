package com.teamchallenge.easybuy.exceptions;

import com.teamchallenge.easybuy.exceptions.goods.CategoryAttributeException;
import com.teamchallenge.easybuy.exceptions.goods.CategoryNotFoundException;
import com.teamchallenge.easybuy.exceptions.goods.GoodsAttributeValueException;
import com.teamchallenge.easybuy.exceptions.goods.GoodsImageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for centralized error handling.
 * This handler catches specific exceptions (e.g., CategoryNotFoundException, GoodsImageException, etc.)
 * and returns a JSON response with appropriate HTTP status codes (e.g., 404 for Not Found, 500 for Internal Server Error).
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
     * Handles all other exceptions with HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
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
        response.put("path", request.getDescription(false));
        return new ResponseEntity<>(response, status);
    }
}