package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Author;
import lombok.Builder;

@Builder
public record AuthorResponse(
        Long authorId,
        String authorName
)
{
    public static AuthorResponse fromEntity(Author author) {
        return AuthorResponse.builder()
                .authorId(author.getAuthorId())
                .authorName(author.getAuthorName())
                .build();
    }
}
