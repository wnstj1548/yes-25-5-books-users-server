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
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> find(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.findCategory(categoryId));
    }

    @GetMapping("/categories/root")
    public ResponseEntity<List<CategoryResponse>> findRoot() {
        return ResponseEntity.ok(categoryService.findRootCategories());
    }

    @GetMapping("/categories/parent/{parentId}")
    public ResponseEntity<List<CategoryResponse>> findByParentCategoryId(@PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(categoryService.findCategoryByParentCategoryId(parentId));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> create(@RequestBody CreateCategoryRequest createCategoryRequest) {
        return ResponseEntity.ok(categoryService.createCategory(createCategoryRequest));
    }

    @PutMapping("/categories")
    public ResponseEntity<CategoryResponse> update(@RequestBody UpdateCategoryRequest updateCategoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(updateCategoryRequest));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
