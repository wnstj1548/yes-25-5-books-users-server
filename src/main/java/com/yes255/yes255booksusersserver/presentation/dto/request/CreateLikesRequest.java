package com.yes255.yes255booksusersserver.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateLikesRequest(

        @NotNull(message = "책은 필수 입력 항목입니다.")
        Long bookId,

        @NotNull(message = "유저는 필수 입력 항목입니다.")
        Long userId
) {

}