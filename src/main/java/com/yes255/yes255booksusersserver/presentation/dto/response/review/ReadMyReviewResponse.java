package com.yes255.yes255booksusersserver.presentation.dto.response.review;

import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.ReviewImage;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ReadMyReviewResponse(Long reviewId,
                                   LocalDate createdAt,
                                   String title,
                                   String content,
                                   Integer rating,
                                   String bookName,
                                   List<String> reviewImages) {

    public static ReadMyReviewResponse fromEntity(Review review) {
        List<String> reviewImages = review.getReviewImage().stream()
            .map(ReviewImage::getReviewImageUrl)
            .toList();

        return ReadMyReviewResponse.builder()
            .reviewId(review.getReviewId())
            .createdAt(review.getReviewTime())
            .title(review.getTitle())
            .content(review.getContent())
            .rating(review.getRating())
            .bookName(review.getBook().getBookName())
            .reviewImages(reviewImages)
            .build();
    }
}