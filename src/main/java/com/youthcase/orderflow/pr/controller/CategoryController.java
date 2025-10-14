package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.pr.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{kanCode}")
    public Category getCategory(@PathVariable String kanCode) {
        return categoryService.getCategoryById(kanCode)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Category not found: " + kanCode));
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @PutMapping("/{kanCode}")
    public Category updateCategory(@PathVariable String kanCode, @RequestBody Category category) {
        category.setKanCode(kanCode);
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/{kanCode}")
    public void deleteCategory(@PathVariable String kanCode) {
        categoryService.deleteCategory(kanCode);
    }
}