package com.esiea.library.catalog.domain;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
