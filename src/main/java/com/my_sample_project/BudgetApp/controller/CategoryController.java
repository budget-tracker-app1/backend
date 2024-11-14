package com.my_sample_project.BudgetApp.controller;

import com.my_sample_project.BudgetApp.dto.category.CategoryDTO;
import com.my_sample_project.BudgetApp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<CategoryDTO>> getUserCategories(@RequestHeader("Authorization") String authHeader) {
        List<CategoryDTO> categories = categoryService.getCategoriesByUserToken(authHeader);
        return ResponseEntity.ok(categories);
    }

    // Endpoint to save a new category
    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(authHeader, categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
}
