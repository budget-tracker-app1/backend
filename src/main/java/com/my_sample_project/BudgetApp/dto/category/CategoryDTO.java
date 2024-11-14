package com.my_sample_project.BudgetApp.dto.category;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CategoryDTO {

    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name can't be longer than 255 characters")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    @Size(max = 32, message = "Color can't be longer than 32 characters")
    private String color;

    @NotBlank(message = "User ID is required")
    private Long user_id;
}
