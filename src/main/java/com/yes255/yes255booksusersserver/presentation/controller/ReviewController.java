package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.ReviewService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.CreateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewRatingResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    public void createReview(@RequestPart("createReviewRequest") String createReviewRequestJson,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @CurrentUser JwtUserDetails jwtUserDetails) {
        CreateReviewRequest createReviewRequest = null;
        try {
            createReviewRequest = objectMapper.readValue(createReviewRequestJson, CreateReviewRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        reviewService.createReview(createReviewRequest, images, jwtUserDetails.userId());
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<Page<ReadReviewResponse>> getReviews(@PathVariable Long bookId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByPaging(bookId, pageable));
    }

    @GetMapping("/books/{bookId}/ratings")
    public ResponseEntity<List<ReadReviewRatingResponse>> getReviewRatings(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewRatingsByBookId(bookId));
    }
}
