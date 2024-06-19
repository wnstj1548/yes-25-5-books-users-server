package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import jakarta.validation.constraints.NotBlank;

public record CreateTagRequest(

        @NotBlank(message = "태그 이름은 필수 입력 항목입니다.")
        String tagName
) {
    public Tag toEntity() {
        return Tag.builder()
                .tagId(null)
                .tagName(tagName)
                .build();
    }
}