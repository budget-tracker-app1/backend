package com.my_sample_project.BudgetApp.dto.error;

import com.my_sample_project.BudgetApp.dto.api.ApiResponseDTO;

public class ErrorResponseDTO extends ApiResponseDTO {
    private int statusCode;
    private String error;

    public ErrorResponseDTO(String message, int statusCode, String error) {
        super(false, message);
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}
