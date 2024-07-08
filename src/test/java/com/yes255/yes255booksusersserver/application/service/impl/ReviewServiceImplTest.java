package com.yes255.yes255booksusersserver.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.common.exception.AccessDeniedException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.EntityNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.OrderAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.PointLog;
import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.ReviewImage;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaReviewImageRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaReviewRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.CreateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.UpdateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewRatingResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private JpaReviewRepository reviewRepository;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaReviewImageRepository reviewImageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JpaPointRepository pointRepository;

    @Mock
    private JpaPointLogRepository pointLogRepository;

    @Mock
    private OrderAdaptor orderAdaptor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private CreateReviewRequest createReviewRequest;
    private UpdateReviewRequest updateReviewRequest;
    private User user;
    private Book book;
    private ReviewImage reviewImage;
    private Review review;
    private Point point;
    private PointLog pointLog;

    @BeforeEach
    void setUp() {
        book = Book.builder()
            .bookId(1L)
            .build();

        user = User.builder()
            .userId(1L)
            .userName("흐으음")
            .build();

        createReviewRequest = CreateReviewRequest.builder()
            .bookId(1L)
            .subject("long패딩보다 긴거는")
            .message("double코트")
            .rating(5)
            .build();

        updateReviewRequest = UpdateReviewRequest.builder()
            .bookId(1L)
            .subject("long패딩보다 짧은거는")
            .message("int패딩")
            .rating(5)
            .build();

        reviewImage = ReviewImage.builder()
            .reviewImageId(1L)
            .reviewImageUrl("http://localhost:8080")
            .build();

        review = Review.builder()
            .reviewId(1L)
            .user(user)
            .book(book)
            .rating(5)
            .reviewImage(List.of(reviewImage))
            .build();

        point = Point.builder()
            .pointCurrent(BigDecimal.ZERO)
            .user(user)
            .build();

        pointLog = PointLog.builder()
            .point(point)
            .pointLogId(1L)
            .pointLogUpdatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("리뷰 생성 요청을 했을 때 리뷰가 생성되는지 확인한다.")
    void createReview() {
        // given
        Book book = Book.builder().bookId(1L).build();
        User user = User.builder().build();

        when(orderAdaptor.existOrderHistory()).thenReturn(true);
        when(reviewRepository.existsByUser_UserIdAndBook_BookId(anyLong(), anyLong())).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(point);

        // when
        reviewService.createReview(createReviewRequest, null, 1L);

        // then
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("주문한 적이 없으면 예외를 반환하는지 확인한다.")
    void createReviewWhenOrderNotFound() {
        // given
        when(orderAdaptor.existOrderHistory()).thenReturn(false);

        // when && then
        assertThatThrownBy(() ->
            reviewService.createReview(createReviewRequest, null, 1L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("리뷰를 남긴적이 있으면 예외를 반환하는지 확인한다.")
    void createReviewWhenHasReview() {
        // given
        when(orderAdaptor.existOrderHistory()).thenReturn(true);
        when(reviewRepository.existsByUser_UserIdAndBook_BookId(anyLong(), anyLong())).thenReturn(true);

        // when && then
        assertThatThrownBy(() ->
            reviewService.createReview(createReviewRequest, null, 1L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("유저가 존재하지 않으면 예외를 반환하는지 확인한다.")
    void createReviewWhenUserNotFound() {
        // given
        when(orderAdaptor.existOrderHistory()).thenReturn(true);
        when(reviewRepository.existsByUser_UserIdAndBook_BookId(anyLong(), anyLong())).thenReturn(false);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() ->
            reviewService.createReview(createReviewRequest, null, 1L))
            .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("리뷰 생성 요청을 했을 때 이미지와 리뷰가 생성되는지 확인한다.")
    void createReviewWhenImageUpload() throws JsonProcessingException {
        // given
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image".getBytes());
        List<MultipartFile> images = List.of(image);

        when(orderAdaptor.existOrderHistory()).thenReturn(true);
        when(reviewRepository.existsByUser_UserIdAndBook_BookId(anyLong(), anyLong())).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        JsonNode fileNode = mock(JsonNode.class);
        when(fileNode.path("url")).thenReturn(fileNode);
        when(fileNode.asText()).thenReturn("http://mockurl.com/image.jpg");

        JsonNode responseBody = mock(JsonNode.class);
        when(responseBody.path("file")).thenReturn(fileNode);
        when(objectMapper.readTree(anyString())).thenReturn(responseBody);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"file\": {\"url\": \"http://mockurl.com/image.jpg\"}}", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(responseEntity);
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(point);

        // when
        reviewService.createReview(createReviewRequest, images, 1L);

        // then
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(reviewImageRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("책을 찾을 수 없을 때 예외를 발생시키는지 확인한다.")
    void createReviewWhenBookNotFound() {
        // given && when
        when(orderAdaptor.existOrderHistory()).thenReturn(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(
            () -> reviewService.createReview(createReviewRequest, null, 1L))
            .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("페이징된 리뷰 목록 요청을 했을 때 성공적으로 리뷰 목록을 반환하는지 확인한다.")
    void getReviewsByPaging() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Review> reviews = new PageImpl<>(Collections.singletonList(review));

        when(reviewRepository.findAllByBook_BookIdAndIsActiveTrueOrderByReviewTimeDesc(anyLong(), any(PageRequest.class)))
            .thenReturn(reviews);

        // when
        Page<ReadReviewResponse> response = reviewService.getReviewsByPaging(1L, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    @DisplayName("리뷰를 업데이트할 때, 요청한 유저 정보가 다르면 예외를 반환하는지 확인한다.")
    void updateReviewWhenUserIdNotEqual() {
        // given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        // when && then
        assertThatThrownBy(() ->
            reviewService.updateReview(updateReviewRequest, null, 1L, 2L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("페이징된 리뷰 목록 요청을 했을 때 사용자 이름을 마스킹처리하는지 확인한다.")
    void getReviewsByPagingWhenUserMasking() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Review> reviews = new PageImpl<>(Collections.singletonList(review));

        when(reviewRepository.findAllByBook_BookIdAndIsActiveTrueOrderByReviewTimeDesc(anyLong(), any(PageRequest.class)))
            .thenReturn(reviews);

        // when
        Page<ReadReviewResponse> response = reviewService.getReviewsByPaging(1L, pageable);
        String maskingUserName = response.getContent().stream()
            .map(ReadReviewResponse::userName)
                .collect(Collectors.joining(", "));

        // then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertThat(maskingUserName).contains("*");
    }

    @DisplayName("리뷰 별 개수를 반환하는지 확인한다.")
    @Test
    void getReviewRatings() {
        // given
        List<Review> reviews = List.of(review);
        when(reviewRepository.findAllByBook_BookIdAndIsActiveTrue(anyLong())).thenReturn(reviews);

        // when
        List<ReadReviewRatingResponse> responses = reviewService.getReviewRatingsByBookId(1L);

        // then
        assertNotNull(responses);
        assertThat(responses.getFirst().rating()).isEqualTo(review.getRating());
    }

    @Test
    @DisplayName("리뷰 업데이트 요청을 했을 때 성공적으로 리뷰가 업데이트되는지 확인한다.")
    void updateReview() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewImageRepository.findAllByReview(review)).thenReturn(List.of());

        // when
        reviewService.updateReview(updateReviewRequest, null, 1L, 1L);

        // then
        assertThat(review.getTitle()).isEqualTo(updateReviewRequest.subject());
    }

    @Test
    @DisplayName("리뷰 업데이트 요청을 했을 때 리뷰를 찾을 수 없을 때 예외를 발생시키는지 확인한다.")
    void updateReviewWhenReviewNotFound() {
        // given && when

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(
            () -> reviewService.updateReview(updateReviewRequest, null, 1L, 1L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 업데이트 요청을 했을 때 이미지와 리뷰가 수정되는지 확인한다.")
    void updateReviewWhenImageUpload() throws JsonProcessingException {
        // given
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image".getBytes());
        String mockUrl = "http://mockurl.com/image.jpg";
        List<MultipartFile> images = List.of(image);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewImageRepository.findAllByReview(review)).thenReturn(List.of(reviewImage));

        JsonNode fileNode = mock(JsonNode.class);
        when(fileNode.path("url")).thenReturn(fileNode);
        when(fileNode.asText()).thenReturn(mockUrl);

        JsonNode responseBody = mock(JsonNode.class);
        when(responseBody.path("file")).thenReturn(fileNode);
        when(objectMapper.readTree(anyString())).thenReturn(responseBody);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"file\": {\"url\": \"http://mockurl.com/image.jpg\"}}", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(responseEntity);

        // when
        reviewService.updateReview(updateReviewRequest, images, 1L, 1L);

        // then
        assertThat(review.getReviewImage().getFirst().reviewImageUrl).isEqualTo(mockUrl);
    }

    @Test
    @DisplayName("리뷰 삭제 요청을 했을 때 성공적으로 리뷰가 삭제되는지 확인한다.")
    void deleteReviewByReviewId() {
        // given
        Review deleteReview = Review.builder()
            .reviewId(1L)
            .isActive(true)
            .reviewImage(List.of(reviewImage))
            .user(user)
            .build();

        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(point);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(deleteReview));

        // when
        reviewService.deleteReviewByReviewId(1L, 1L);

        // then
        assertThat(deleteReview.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("리뷰 삭제 요청을 했을 때 리뷰를 찾을 수 없을 때 예외를 발생시키는지 확인한다.")
    void deleteReviewByReviewIdWhenReviewNotFound() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
            () -> reviewService.deleteReviewByReviewId(1L, 1L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰를 삭제할 때, 요청한 유저 정보가 다르면 예외를 반환하는지 확인한다.")
    void deleteReviewWhenUserIdNotEqual() {
        // given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        // when && then
        assertThatThrownBy(() ->
            reviewService.deleteReviewByReviewId(1L, 2L))
            .isInstanceOf(AccessDeniedException.class);
    }
}
