package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Category;
import lombok.Builder;

@Builder
public record CategoryResponse(Long categoryId, String categoryName, Long parentCategoryId)
{

}
