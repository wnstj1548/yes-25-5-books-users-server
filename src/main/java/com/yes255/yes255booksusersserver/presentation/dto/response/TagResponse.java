package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import lombok.Builder;

@Builder
public record TagResponse(Long tagId, String tagName) {
    public static TagResponse fromEntity(Tag tag) {
        return TagResponse.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .build();
    }
}
