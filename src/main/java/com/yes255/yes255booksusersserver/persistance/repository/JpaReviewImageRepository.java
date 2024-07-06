package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.ReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReview(Review review);
}
