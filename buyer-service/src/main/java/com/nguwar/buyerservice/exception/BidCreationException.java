package com.nguwar.buyerservice.exception;

public class BidCreationException extends RuntimeException{
    public BidCreationException(String errorMessage) {
        super(errorMessage);
    }
}
