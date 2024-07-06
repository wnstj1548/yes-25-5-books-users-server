package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.review.CreateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.UpdateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewRatingResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {

    void createReview(CreateReviewRequest createReviewRequest, List<MultipartFile> images,
        Long userId);

    Page<ReadReviewResponse> getReviewsByPaging(Long bookId, Pageable pageable);

    List<ReadReviewRatingResponse> getReviewRatingsByBookId(Long bookId);

    void updateReview(UpdateReviewRequest updateReviewRequest, List<MultipartFile> images, Long reviewId);

    void deleteReviewByReviewId(Long reviewId);
}
