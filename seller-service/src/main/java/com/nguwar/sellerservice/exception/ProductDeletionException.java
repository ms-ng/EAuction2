package com.nguwar.sellerservice.exception;

public class ProductDeletionException extends RuntimeException{
    public ProductDeletionException(String errorMessage) {
        super(errorMessage);
    }
}
