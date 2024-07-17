package com.yes255.yes255booksusersserver.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateBookCategoryRequest(

        @NotNull(message = "북 카테고리 아이디는 필수 입력 항목입니다.")
        Long bookCategoryId,

        @NotNull(message = "업데이트 할 책은 필수 입력 항목입니다.")
        Long bookId,

        @NotNull(message = "업데이트 할 카테고리는 필수 입력 항목입니다.")
        Long categoryId) {
}