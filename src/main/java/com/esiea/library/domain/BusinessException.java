package com.esiea.library.domain;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
