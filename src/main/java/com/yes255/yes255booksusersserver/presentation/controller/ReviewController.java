package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.ReviewService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.CreateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.UpdateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadMyReviewResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewRatingResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewResponse;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "리뷰 API", description = "리뷰 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다.")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Void> createReview(@RequestPart("createReviewRequest") String createReviewRequestJson,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @CurrentUser JwtUserDetails jwtUserDetails) {
        CreateReviewRequest createReviewRequest = jsonToRequest(createReviewRequestJson,
            CreateReviewRequest.class);

        reviewService.createReview(createReviewRequest, images, jwtUserDetails.userId());

        return ResponseEntity.ok()
            .headers(addAuthHeaders(jwtUserDetails))
            .build();
    }

//    @Operation(summary = "")
    @GetMapping("/books/{bookId}")
    public ResponseEntity<Page<ReadReviewResponse>> getReviews(@PathVariable Long bookId,
        Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByPaging(bookId, pageable));
    }

    @GetMapping("/books/{bookId}/ratings")
    public ResponseEntity<List<ReadReviewRatingResponse>> getReviewRatings(
        @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewRatingsByBookId(bookId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
        @RequestPart String requestJson,
        @RequestPart(required = false) List<MultipartFile> images,
        @CurrentUser JwtUserDetails jwtUserDetails) {
        UpdateReviewRequest updateReviewRequest = jsonToRequest(requestJson, UpdateReviewRequest.class);
        reviewService.updateReview(updateReviewRequest, images, reviewId, jwtUserDetails.userId());

        return ResponseEntity.noContent()
            .headers(addAuthHeaders(jwtUserDetails))
            .build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
        @CurrentUser JwtUserDetails jwtUserDetails) {
        reviewService.deleteReviewByReviewId(reviewId, jwtUserDetails.userId());

        return ResponseEntity.noContent()
            .headers(addAuthHeaders(jwtUserDetails))
            .build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<ReadMyReviewResponse>> getMyReviews(Pageable pageable,
        @CurrentUser JwtUserDetails jwtUserDetails) {

        return ResponseEntity.ok()
            .headers(addAuthHeaders(jwtUserDetails))
            .body(reviewService.getReviewsByUserId(jwtUserDetails.userId(), pageable));
    }

    private HttpHeaders addAuthHeaders(JwtUserDetails jwtUserDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUserDetails.accessToken());
        headers.set("Refresh-Token", jwtUserDetails.refreshToken());
        return headers;
    }

    private <T> T jsonToRequest(String requestJson, Class<T> clazz) {
        try {
            return objectMapper.readValue(requestJson, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
