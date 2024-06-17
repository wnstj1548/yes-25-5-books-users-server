package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Tag;

public record UpdateTagRequest(Long tagId, String tagName) {
    public Tag toEntity() {
        return Tag.builder()
                .tagId(tagId)
                .tagName(tagName)
                .build();
    }
}
