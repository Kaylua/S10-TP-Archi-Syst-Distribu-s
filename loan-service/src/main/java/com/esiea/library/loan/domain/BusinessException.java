package com.esiea.library.loan.domain;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
