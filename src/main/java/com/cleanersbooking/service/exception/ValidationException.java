package com.cleanersbooking.service.exception;

public class ValidationException extends RuntimeException {
    private Long code;
    private String message;

    public ValidationException(Long code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
