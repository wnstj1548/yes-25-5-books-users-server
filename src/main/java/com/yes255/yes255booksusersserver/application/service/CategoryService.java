package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    CategoryResponse findCategory(long categoryId);

    List<CategoryResponse> findAllCategories();

    CategoryResponse updateCategory(UpdateCategoryRequest updateCategoryRequest);

    void deleteCategory(long categoryId);

    List<CategoryResponse> findRootCategories();

    List<CategoryResponse> findCategoryByParentCategoryId(long parentCategoryId);
}
