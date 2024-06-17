package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CategoryService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.findByCategoryId(categoryId));
    }

    @GetMapping("/categories/first")
    public ResponseEntity<List<CategoryResponse>> getFirstStepCategory() {
        return ResponseEntity.ok(categoryService.findFirstStepCategories());
    }

    @GetMapping("/categories/parent/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getParentCategory(@PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(categoryService.findByParentCategoryId(parentId));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        return ResponseEntity.ok(categoryService.createCategory(createCategoryRequest));
    }

    @PatchMapping("/categories")
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody UpdateCategoryRequest updateCategoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(updateCategoryRequest));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteByCategoryId(categoryId);
        return ResponseEntity.noContent().build();
    }

}
