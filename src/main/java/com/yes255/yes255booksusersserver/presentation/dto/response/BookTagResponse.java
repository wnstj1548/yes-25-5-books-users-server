package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record BookTagResponse (Long bookTagId, Long bookId, Long tagId) {
}
