package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Category;

public record UpdateCategoryRequest(long categoryId, String categoryName, Category parentCategory) {

    public Category toEntity() {

        return Category.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .parentCategory(parentCategory)
                .build();
    }
}
