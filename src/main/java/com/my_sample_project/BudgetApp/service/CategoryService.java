package com.my_sample_project.BudgetApp.service;

import com.my_sample_project.BudgetApp.dto.category.CategoryDTO;
import com.my_sample_project.BudgetApp.dto.category.CategoryProjection;
import com.my_sample_project.BudgetApp.model.Category;
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
        // Validate Bearer token format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format.");
        }

        // Extract token from "Bearer <token>"
        String token = authHeader.substring(7);

        // Extract userId from token
        Long userId = jwtUtil.extractUserId(token);

        // Fetch categories by userId
        List<CategoryProjection> projections = categoryRepository.findByUserId(userId);

        // Convert projections to DTOs
        return projections.stream()
                .map(projection -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setId(projection.getId());
                    categoryDTO.setName(projection.getName());
                    categoryDTO.setType(projection.getType());
                    categoryDTO.setColor(projection.getColor());
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    // Save a new category for the authenticated user
    public CategoryDTO saveCategory(String authHeader, CategoryDTO categoryDTO) {
        // Validate Bearer token format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format.");
        }

        // Extract token from "Bearer <token>"
        String token = authHeader.substring(7);
        System.out.println("Received Token: " + token);

        // Extract userId from token
        Long userId = jwtUtil.extractUserId(token);
        System.out.println("Extracted UserId: " + userId);

        // Create and save new category
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setType(categoryDTO.getType());
        category.setColor(categoryDTO.getColor());
        category.setUserId(userId);  // Set the userId dynamically from the token

        category = categoryRepository.save(category);  // Save category to database

        // Convert saved Category to CategoryDTO
        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setId(category.getId());
        savedCategoryDTO.setName(category.getName());
        savedCategoryDTO.setType(category.getType());
        savedCategoryDTO.setColor(category.getColor());

        return savedCategoryDTO;
    }
}
