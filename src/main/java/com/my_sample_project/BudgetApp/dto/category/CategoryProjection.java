package com.my_sample_project.BudgetApp.dto.category;

import com.my_sample_project.BudgetApp.model.CategoryType;

public interface CategoryProjection {
    Integer getId();
    String getName();
    CategoryType getType();
    String getColor();
    Long getUserId();
    Double getBalance();
}
