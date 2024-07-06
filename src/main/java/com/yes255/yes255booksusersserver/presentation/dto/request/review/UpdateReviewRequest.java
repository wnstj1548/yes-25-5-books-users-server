package com.yes255.yes255booksusersserver.presentation.dto.request.review;

public record UpdateReviewRequest(String name, String subject, String message, Long bookId, Integer rating) {
}
