package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    CategoryResponse findByCategoryId(long categoryId);

    List<CategoryResponse> findAllCategories();

    CategoryResponse updateCategory(UpdateCategoryRequest updateCategoryRequest);

    void deleteByCategoryId(long categoryId);

    List<CategoryResponse> findFirstStepCategories();

    List<CategoryResponse> findByParentCategoryId(long parentCategoryId);
}
