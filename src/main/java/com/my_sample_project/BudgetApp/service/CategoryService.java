package com.my_sample_project.BudgetApp.service;

import com.my_sample_project.BudgetApp.dto.category.CategoryDTO;
import com.my_sample_project.BudgetApp.dto.category.CategoryProjection;
import com.my_sample_project.BudgetApp.exception.CategoryValidationException;
import com.my_sample_project.BudgetApp.exception.ResourceNotFoundException;
import com.my_sample_project.BudgetApp.model.Category;
import com.my_sample_project.BudgetApp.model.CategoryType;
import com.my_sample_project.BudgetApp.repository.CategoryRepository;
import com.my_sample_project.BudgetApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, JwtUtil jwtUtil) {
        this.categoryRepository = categoryRepository;
        this.jwtUtil = jwtUtil;
    }

    // Fetch categories based on the user ID from the Bearer token
    public List<CategoryDTO> getCategoriesByUserToken(String authHeader) {
        // Extract userId from token
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);

        // Fetch categories by userId
        List<CategoryProjection> projections = categoryRepository.findByUserId(userId);

        // Convert projections to DTOs
        return projections.stream()
                .map(projection -> new CategoryDTO(
                        projection.getId(),
                        projection.getName(),
                        projection.getType(),
                        projection.getColor(),
                        projection.getUserId()
                ))
                .collect(Collectors.toList());
    }

    public void validateNameAndType(String name, CategoryType type, Long userId) {
        // Check if category with the same name and type already exists for this user
        boolean exists = categoryRepository.existsByNameAndTypeAndUserId(name, type, userId);
        if (exists) {
            throw new CategoryValidationException("Category with this name and type already exists.", 400);
        }
    }

    // Helper method to create or update a category and return the corresponding CategoryDTO
    private CategoryDTO saveOrUpdateCategory(Long userId, Integer id, CategoryDTO categoryDTO) {
        try {
            Category category;

            if (id != null) {
                // Update existing category
                category = categoryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Category not found"));
            } else {
                // Create new category
                category = new Category();
            }

            // Set properties
            category.setName(categoryDTO.getName());

            // Convert String to CategoryType enum
            category.setType(categoryDTO.getType());

            category.setColor(categoryDTO.getColor());
            category.setUserId(userId);

            // Save or update the category in the database
            category = categoryRepository.save(category);

            return new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getType(),
                    category.getColor(),
                    category.getUserId()
            );
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    // Save a new category for the authenticated user
    public CategoryDTO saveCategory(String authHeader, CategoryDTO categoryDTO) {
        // Extract userId from token
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);

        // Validate name and type uniqueness for the user
        validateNameAndType(categoryDTO.getName(), categoryDTO.getType(), userId);

        // Use helper method to create and save the category
        return saveOrUpdateCategory(userId, null, categoryDTO);
    }

    // Update an existing category for the authenticated user
    public CategoryDTO updateCategory(String authHeader, Integer id, CategoryDTO categoryDTO) {
        // Extract userId from token
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);

        // Use helper method to update the category
        return saveOrUpdateCategory(userId, id, categoryDTO);
    }
}
