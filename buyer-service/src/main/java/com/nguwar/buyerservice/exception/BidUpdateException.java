package com.nguwar.buyerservice.exception;

public class BidUpdateException extends RuntimeException{
    public BidUpdateException(String errorMessage) {
        super(errorMessage);
    }
}
