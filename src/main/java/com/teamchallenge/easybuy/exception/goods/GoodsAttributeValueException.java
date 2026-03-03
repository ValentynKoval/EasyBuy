package com.teamchallenge.easybuy.exception.goods;

public class GoodsAttributeValueException extends RuntimeException {

    public GoodsAttributeValueException(String message) {
        super(message);
    }

    public GoodsAttributeValueException(String message, Throwable cause) {
        super(message, cause);
    }
}