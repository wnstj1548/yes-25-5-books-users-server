package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Tag;

public record CreateTagRequest(String tagName) {
    public Tag toEntity() {
        return Tag.builder()
                .tagId(null)
                .tagName(tagName)
                .build();
    }
}
