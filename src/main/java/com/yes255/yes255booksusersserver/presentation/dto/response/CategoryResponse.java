package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Category;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Builder
public record CategoryResponse(
        Long categoryId,
        String categoryName,
        Long parentCategoryId,
        List<CategoryResponse> subCategories
)
{
    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentCategoryId(category.getParentCategory() != null ?
                        category.getParentCategory().getCategoryId() :
                        null)
                .subCategories(category.getSubCategories() != null ?
                        category.getSubCategories().stream().map(CategoryResponse::fromEntity).toList()
                        : Collections.emptyList())
                .build();
    }
}
