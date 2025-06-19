package com.teamchallenge.easybuy.exceptions.goods;

public class GoodsImageException extends RuntimeException {

    public GoodsImageException(String message) {
        super(message);
    }

    public GoodsImageException(String message, Throwable cause) {
        super(message, cause);
    }
}