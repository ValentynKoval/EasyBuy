package com.teamchallenge.easybuy.exception.shop;

/**
 * Signals failures while interacting with external billing providers for a shop.
 */
public class ShopBillingIntegrationException extends RuntimeException {

    public ShopBillingIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

