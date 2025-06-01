package com.teamchallenge.easybuy.exceptions.goods;

import java.util.UUID;

/**
     * Exception thrown when no products are found for the specified category.
 * */
public class GoodsNotFoundException extends RuntimeException {

    public GoodsNotFoundException(String message) {
        super(message);
    }

    public GoodsNotFoundException(UUID categoryId) {
        super("No goods found for category with id: " + categoryId);
    }
}