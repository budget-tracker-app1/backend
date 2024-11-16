package com.my_sample_project.BudgetApp.exception;

public class CategoryAlreadyExistsException extends RuntimeException {

    private final int statusCode;

    public CategoryAlreadyExistsException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
