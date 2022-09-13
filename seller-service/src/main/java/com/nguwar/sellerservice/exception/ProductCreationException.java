package com.nguwar.sellerservice.exception;

public class ProductCreationException extends RuntimeException{
    public ProductCreationException(String errorMessage) {
        super(errorMessage);
    }
}
