package com.my_sample_project.BudgetApp.repository;

import com.my_sample_project.BudgetApp.dto.category.CategoryProjection;
import com.my_sample_project.BudgetApp.model.Category;
import com.my_sample_project.BudgetApp.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<CategoryProjection> findByUserId(Long userId);
    boolean existsByNameAndTypeAndUserId(String name, CategoryType type, Long userId);
    Optional<Category> findByNameAndTypeAndUserId(String name, CategoryType type, Long userId);
}
