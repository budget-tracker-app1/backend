package com.my_sample_project.BudgetApp.dto.api;

import java.util.Map;

public class ApiResponseDTO {
    private boolean success;
    private String message;
    private Map<String, Object> data;

    // Constructor with data
    public ApiResponseDTO(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor without data
    public ApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
