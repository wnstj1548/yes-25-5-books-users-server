package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.ReviewService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.CreateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.UpdateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadMyReviewResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewRatingResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ReviewController reviewController;

    private JwtUserDetails jwtUserDetails;
    private CreateReviewRequest createReviewRequest;
    private UpdateReviewRequest updateReviewRequest;
    private ResponseEntity<Page<ReadReviewResponse>> getReviewsResponse;
    private ResponseEntity<Page<ReadMyReviewResponse>> getMyReviewsResponse;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "dummyAccessToken", "dummyRefreshToken");

        createReviewRequest = CreateReviewRequest.builder()
                .name("User")
                .subject("Great Book")
                .rating(5)
                .message("Loved it!")
                .bookId(1L)
                .build();

        updateReviewRequest = UpdateReviewRequest.builder()
                .name("User")
                .subject("Updated Title")
                .message("Updated Content")
                .bookId(1L)
                .rating(4)
                .build();

        List<ReadReviewResponse> reviews = Collections.singletonList(new ReadReviewResponse(1L, "Test Title", "Test Content", 5, LocalDate.now(), "User", 1L, List.of("image_url")));
        Page<ReadReviewResponse> reviewPage = new PageImpl<>(reviews);
        getReviewsResponse = ResponseEntity.ok(reviewPage);

        List<ReadMyReviewResponse> myReviews = Collections.singletonList(new ReadMyReviewResponse(1L, LocalDate.now(), "My Review", "Content", 5, "Book Title", List.of("image_url")));
        Page<ReadMyReviewResponse> myReviewPage = new PageImpl<>(myReviews);
        getMyReviewsResponse = ResponseEntity.ok(myReviewPage);
    }

    @Test
    @DisplayName("리뷰 생성 - 성공")
    void testCreateReview() throws Exception {
        String requestJson = "{\"title\": \"Great Book\", \"content\": \"Loved it!\", \"rating\": 5}";

        when(objectMapper.readValue(requestJson, CreateReviewRequest.class)).thenReturn(createReviewRequest);

        ResponseEntity<Void> response = reviewController.createReview(requestJson, Collections.emptyList(), jwtUserDetails);

        verify(reviewService, times(1)).createReview(createReviewRequest, Collections.emptyList(), 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bearer dummyAccessToken", response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        assertEquals("dummyRefreshToken", response.getHeaders().get("Refresh-Token").get(0));
    }

    @Test
    @DisplayName("리뷰 조회 - 성공")
    void testGetReviews() {
        Long bookId = 1L;
        Pageable pageable = Pageable.unpaged();

        when(reviewService.getReviewsByPaging(bookId, pageable)).thenReturn(getReviewsResponse.getBody());

        ResponseEntity<Page<ReadReviewResponse>> response = reviewController.getReviews(bookId, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getReviewsResponse.getBody().getContent(), response.getBody().getContent());
        verify(reviewService, times(1)).getReviewsByPaging(bookId, pageable);
    }

    @Test
    @DisplayName("리뷰 수정 - 성공")
    void testUpdateReview() throws Exception {
        Long reviewId = 1L;
        String requestJson = "{\"title\": \"Updated Title\", \"content\": \"Updated Content\", \"rating\": 4}";

        when(objectMapper.readValue(requestJson, UpdateReviewRequest.class)).thenReturn(updateReviewRequest);

        ResponseEntity<Void> response = reviewController.updateReview(reviewId, requestJson, Collections.emptyList(), jwtUserDetails);

        verify(reviewService, times(1)).updateReview(updateReviewRequest, Collections.emptyList(), reviewId, 1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Bearer dummyAccessToken", response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        assertEquals("dummyRefreshToken", response.getHeaders().get("Refresh-Token").get(0));
    }

    @Test
    @DisplayName("리뷰 삭제 - 성공")
    void testDeleteReview() {
        Long reviewId = 1L;

        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, jwtUserDetails);

        verify(reviewService, times(1)).deleteReviewByReviewId(reviewId, 1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Bearer dummyAccessToken", response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        assertEquals("dummyRefreshToken", response.getHeaders().get("Refresh-Token").get(0));
    }

    @Test
    @DisplayName("내 리뷰 조회 - 성공")
    void testGetMyReviews() {
        Pageable pageable = Pageable.unpaged();

        when(reviewService.getReviewsByUserId(1L, pageable)).thenReturn(getMyReviewsResponse.getBody());

        ResponseEntity<Page<ReadMyReviewResponse>> response = reviewController.getMyReviews(pageable, jwtUserDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getMyReviewsResponse.getBody().getContent(), response.getBody().getContent());
        assertEquals("Bearer dummyAccessToken", response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        assertEquals("dummyRefreshToken", response.getHeaders().get("Refresh-Token").get(0));
        verify(reviewService, times(1)).getReviewsByUserId(1L, pageable);
    }

    private HttpHeaders addAuthHeaders(JwtUserDetails jwtUserDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUserDetails.accessToken());
        headers.set("Refresh-Token", jwtUserDetails.refreshToken());
        return headers;
    }
}
