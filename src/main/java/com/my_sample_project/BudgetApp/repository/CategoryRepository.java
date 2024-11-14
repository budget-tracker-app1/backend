package com.my_sample_project.BudgetApp.repository;

import com.my_sample_project.BudgetApp.dto.category.CategoryProjection;
import com.my_sample_project.BudgetApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<CategoryProjection> findByUserId(Long userId);
}
