package com.my_sample_project.BudgetApp.dto.category;

import com.my_sample_project.BudgetApp.model.CategoryType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name can't be longer than 255 characters")
    private String name;

    @NotNull(message = "Type is required")
    private CategoryType type;

    @Size(max = 32, message = "Color can't be longer than 32 characters")
    private String color;

    @NotNull(message = "User ID is required")
    private Long userId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be 0.00 or greater")
    private Double balance;
}
