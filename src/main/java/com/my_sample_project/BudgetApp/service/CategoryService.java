package com.my_sample_project.BudgetApp.service;

import com.my_sample_project.BudgetApp.model.Category;
import com.my_sample_project.BudgetApp.repository.CategoryRepository;
import com.my_sample_project.BudgetApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Category> getCategoriesByUserToken(String authHeader) {
        // Validate Bearer token format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format.");
        }

        // Extract token from "Bearer <token>"
        String token = authHeader.substring(7);

        // Extract userId from token
        Long userId = jwtUtil.extractUserId(token);

        // Fetch categories by userId
        return categoryRepository.findByUserId(userId);
    }
}
