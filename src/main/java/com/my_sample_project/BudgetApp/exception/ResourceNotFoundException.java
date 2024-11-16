package com.my_sample_project.BudgetApp.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final int statusCode;

    public ResourceNotFoundException(String message) {
        super(message);
        this.statusCode = HttpStatus.NOT_FOUND.value(); // HTTP 404
    }

    public ResourceNotFoundException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
