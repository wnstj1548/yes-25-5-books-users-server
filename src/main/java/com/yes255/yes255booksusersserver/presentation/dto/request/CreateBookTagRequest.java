package com.yes255.yes255booksusersserver.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateBookTagRequest(

        @NotNull(message = "책은 필수 입력 항목입니다.")
        Long bookId,

        @NotNull(message = "태그는 필수 입력 항목입니다.")
        Long tagId
) {
}