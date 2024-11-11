package com.my_sample_project.BudgetApp.controller;

import com.my_sample_project.BudgetApp.model.Category;
import com.my_sample_project.BudgetApp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Endpoint to get categories for the authenticated user
    @GetMapping
    public ResponseEntity<List<Category>> getUserCategories(@RequestHeader("Authorization") String authHeader) {
        List<Category> categories = categoryService.getCategoriesByUserToken(authHeader);
        return ResponseEntity.ok(categories);
    }
}
