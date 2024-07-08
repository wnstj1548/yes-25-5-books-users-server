package com.yes255.yes255booksusersserver.presentation.dto.response.review;

import com.yes255.yes255booksusersserver.persistance.domain.Review;
import lombok.Builder;

@Builder
public record ReadReviewRatingResponse(Long reviewId, Integer rating) {

    public static ReadReviewRatingResponse fromEntity(Review review) {
        return ReadReviewRatingResponse.builder()
            .reviewId(review.getReviewId())
            .rating(review.getRating())
            .build();
    }
}
