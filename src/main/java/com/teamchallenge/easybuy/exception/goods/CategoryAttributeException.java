package com.teamchallenge.easybuy.exception.goods;

public class CategoryAttributeException extends RuntimeException {

    public CategoryAttributeException(String message) {
        super(message);
    }

    public CategoryAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}