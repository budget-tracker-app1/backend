package com.my_sample_project.BudgetApp.exception;

public class CategoryValidationException extends RuntimeException {
    private final int statusCode;

    public CategoryValidationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
