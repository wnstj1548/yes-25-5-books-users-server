package com.yes255.yes255booksusersserver.presentation.dto.response.review;

import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.ReviewImage;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ReadReviewResponse(Long reviewId,
                                 String title,
                                 String content,
                                 Integer rating,
                                 LocalDate reviewTime,
                                 String userName,
                                 Long userId,
                                 List<String> reviewImageUrls) {

    public static ReadReviewResponse fromEntity(Review review) {
        List<String> imageUrls = review.getReviewImage().stream()
            .map(ReviewImage::getReviewImageUrl)
            .toList();

        String maskedUserName = maskName(review.getUser().getUserName());

        return ReadReviewResponse.builder()
            .reviewId(review.getReviewId())
            .title(review.getTitle())
            .content(review.getContent())
            .rating(review.getRating())
            .reviewTime(review.getReviewTime())
            .userId(review.getUser().getUserId())
            .userName(maskedUserName)
            .reviewImageUrls(imageUrls)
            .build();
    }

    /**
     * 유저명을 마스킹하는 코드입니다. 2 이하의 이름은 첫번째명을 마스킹처리합니다. 그 이외에는 이름의 절반을 마스킹처리합니다.
     * @param userName 유저명
     * */
    private static String maskName(String userName) {
        if (userName.length() <= 2) {
            return userName.charAt(0) + "*";
        }

        int length = userName.length();
        int starsCount = userName.length() / 2;
        String stars = "*".repeat(starsCount);

        return userName.substring(0, length - starsCount) + stars;
    }
}
