package com.my_sample_project.BudgetApp.dto.error;

import com.my_sample_project.BudgetApp.dto.api.ApiResponseDTO;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponseDTO extends ApiResponseDTO {
    private int statusCode;
    private String error;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String message, int statusCode, String error) {
        super(false, message);
        this.statusCode = statusCode;
        this.error = error;
        this.timestamp = LocalDateTime.now(); // Current timestamp
    }

    @JsonProperty
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty
    public String getError() {
        return error;
    }

    @JsonProperty
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
