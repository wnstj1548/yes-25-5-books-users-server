package com.yes255.yes255booksusersserver.presentation.dto.request.review;

import lombok.Builder;

@Builder
public record UpdateReviewRequest(String name, String subject, String message, Long bookId, Integer rating) {
}
