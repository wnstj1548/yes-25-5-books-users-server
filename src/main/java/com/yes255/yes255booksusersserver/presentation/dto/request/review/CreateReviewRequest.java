package com.yes255.yes255booksusersserver.presentation.dto.request.review;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import java.time.LocalDate;

public record CreateReviewRequest(String name, String subject, Integer rating, String message, Long bookId) {

    public Review toEntity(Book book, User user) {
        return Review.builder()
            .content(message)
            .title(subject)
            .rating(rating)
            .book(book)
            .user(user)
            .reviewTime(LocalDate.now())
            .build();
    }
}
