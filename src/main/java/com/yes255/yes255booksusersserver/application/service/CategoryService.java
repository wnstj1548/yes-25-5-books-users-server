package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    CategoryResponse getCategory(long categoryId);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(UpdateCategoryRequest updateCategoryRequest);

    void removeCategory(long categoryId);

    List<CategoryResponse> getRootCategories();

    List<CategoryResponse> getCategoryByParentCategoryId(long parentCategoryId);

    List<Long> getCategoryIdByBookId(long bookId);
}
