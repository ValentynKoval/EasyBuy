package com.teamchallenge.easybuy.product.exception;

public class CategoryAttributeException extends RuntimeException {

    public CategoryAttributeException(String message) {
        super(message);
    }

    public CategoryAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}