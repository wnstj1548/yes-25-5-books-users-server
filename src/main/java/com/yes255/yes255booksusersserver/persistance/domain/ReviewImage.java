package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewImageId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    public String reviewImageUrl;

    @JoinColumn(nullable = false, name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Review review;

    @Builder
    public ReviewImage(Long reviewImageId, String reviewImageUrl, Review review) {
        this.reviewImageId = reviewImageId;
        this.reviewImageUrl = reviewImageUrl;
        this.review = review;
    }

    public static ReviewImage from(String imageUrl, Review review) {
        return ReviewImage.builder()
            .review(review)
            .reviewImageUrl(imageUrl)
            .build();
    }

    public void updateImageUrl(String imageUrl) {
        this.reviewImageUrl = imageUrl;
    }
}
