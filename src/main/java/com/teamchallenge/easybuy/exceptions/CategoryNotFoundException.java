package com.teamchallenge.easybuy.exceptions;

import java.util.UUID;

/*** Exception thrown when category is not found with the given ID.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(UUID id) {
        super("Category not found with id: " + id);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}