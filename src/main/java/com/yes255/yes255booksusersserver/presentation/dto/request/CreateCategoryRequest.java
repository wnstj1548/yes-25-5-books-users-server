package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Category;

public record CreateCategoryRequest(String categoryName, Category parentCategory) {

    public Category toEntity() {

        return Category.builder()
                .categoryId(null)
                .categoryName(categoryName)
                .parentCategory(parentCategory)
                .build();
    }

}
