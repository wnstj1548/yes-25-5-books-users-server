package com.yes255.yes255booksusersserver.persistance.domain;

import com.yes255.yes255booksusersserver.presentation.dto.request.review.UpdateReviewRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private LocalDate reviewTime;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImage = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String reviewType;

    @Column(nullable = false)
    private Boolean hasChangedToImageReview;

    @Builder
    public Review(Long reviewId, String content, String title, Integer rating, LocalDate reviewTime,
        List<ReviewImage> reviewImage, Book book, User user, String reviewType,
        Boolean hasChangedToImageReview) {
        this.reviewId = reviewId;
        this.content = content;
        this.title = title;
        this.rating = rating;
        this.reviewTime = reviewTime;
        this.reviewImage = reviewImage;
        this.book = book;
        this.user = user;
        this.reviewType = reviewType;
        this.hasChangedToImageReview = hasChangedToImageReview;
    }

    public void updateReview(UpdateReviewRequest updateReviewRequest) {
        if (updateReviewRequest.message() != null) {
            this.content = updateReviewRequest.message();
        }

        if (updateReviewRequest.subject() != null) {
            this.title = updateReviewRequest.subject();
        }

        if (updateReviewRequest.rating() != null) {
            this.rating = updateReviewRequest.rating();
        }
    }

    public boolean isUserIdEqualTo(Long userId) {
        return this.user.getUserId().equals(userId);
    }

    public void updateHasChangedToImageReviewAndReviewType(boolean hasChangedToImageReview,
        String reviewType) {
        this.hasChangedToImageReview = hasChangedToImageReview;
        this.reviewType = reviewType;
    }
}
