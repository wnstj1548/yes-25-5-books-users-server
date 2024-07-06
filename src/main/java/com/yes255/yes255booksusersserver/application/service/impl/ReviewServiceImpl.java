package com.yes255.yes255booksusersserver.application.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.ReviewService;
import com.yes255.yes255booksusersserver.common.exception.AccessDeniedException;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.EntityNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.OrderAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.ReviewImage;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaReviewImageRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaReviewRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.CreateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.review.UpdateReviewRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewRatingResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.review.ReadReviewResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final JpaReviewRepository reviewRepository;
    private final JpaReviewImageRepository reviewImageRepository;
    private final JpaBookRepository bookRepository;
    private final JpaUserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${nhncloud.manager.appkey}")
    private String appKey;

    @Value("${nhncloud.manager.secretKey}")
    private String secretKey;

    private final OrderAdaptor orderAdaptor;
    private final RestTemplate restTemplate;

    @Override
    public void createReview(CreateReviewRequest createReviewRequest, List<MultipartFile> images,
        Long userId) {
        if (!orderAdaptor.existOrderHistory()) {
            throw new AccessDeniedException("주문한 적이 없으면 리뷰를 남길 수 없습니다.");
        }

        if (reviewRepository.existsByUser_UserIdAndBook_BookId(userId, createReviewRequest.bookId())) {
            throw new AccessDeniedException("리뷰를 남긴적이 있으면 다시 리뷰를 작성할 수 없습니다.");
        }

        Book book = bookRepository.findById(createReviewRequest.bookId())
            .orElseThrow(() -> new BookNotFoundException(
                ErrorStatus.toErrorStatus(
                    "해당하는 도서를 찾을 수 없습니다. 도서 ID : {}" + createReviewRequest.bookId(), 404,
                    LocalDateTime.now())));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(
                ErrorStatus.toErrorStatus("유저를 찾을 수 없습니다. 유저 ID : " + userId, 404, LocalDateTime.now())));

        Review review = createReviewRequest.toEntity(book, user);
        Review savedReview = reviewRepository.save(review);

        List<ReviewImage> reviewImages = new ArrayList<>();
        if (!CollectionUtils.isEmpty(images)) {
            for (MultipartFile image : images) {
                String imageUrl = getUploadUrl(image);
                ReviewImage reviewImage = ReviewImage.from(imageUrl, savedReview);

                reviewImages.add(reviewImage);
            }
        }

        reviewImageRepository.saveAll(reviewImages);
        log.info("리뷰가 업로드되었습니다. 요청: {}", createReviewRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReadReviewResponse> getReviewsByPaging(Long bookId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByBook_BookIdOrderByReviewTimeDesc(bookId, pageable);

        return reviews.map(ReadReviewResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadReviewRatingResponse> getReviewRatingsByBookId(Long bookId) {
        List<Review> reviews = reviewRepository.findAllByBook_BookId(bookId);

        return reviews.stream()
            .map(ReadReviewRatingResponse::fromEntity)
            .toList();
    }

    @Override
    public void updateReview(UpdateReviewRequest updateReviewRequest, List<MultipartFile> images,
        Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityNotFoundException("해당하는 리뷰를 찾을 수 없습니다. 리뷰 ID : " + reviewId));

        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(review);

        List<String> uploadUrls = new ArrayList<>();
        if (!CollectionUtils.isEmpty(images)) {
            for (MultipartFile image : images) {
                String imageUrl = getUploadUrl(image);
                uploadUrls.add(imageUrl);
            }
        }

        for (int i = 0; i < reviewImages.size() && i < uploadUrls.size(); i++) {
            reviewImages.get(i).updateImageUrl(uploadUrls.get(i));
        }

        review.updateReview(updateReviewRequest);
        log.info("모든 리뷰 업데이트에 완료하였습니다. 리뷰 ID : {}", reviewId);
    }

    @Override
    public void deleteReviewByReviewId(Long reviewId) {
        reviewRepository.deleteByReviewId(reviewId);
    }

    private String getUploadUrl(MultipartFile image) {
        String fileName = UUID.randomUUID().toString();
        String originalFileName = image.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }

        String path = "/yes25-5-images/review/" + fileName + extension;
        boolean overwrite = true;
        try {
            byte[] imageBytes = image.getBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", secretKey);

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

            String url = String.format(
                "https://api-image.nhncloudservice.com/image/v2.0/appkeys/%s/images?path=%s&overwrite=%s",
                appKey, path, overwrite);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT,
                requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                return responseBody.path("file").path("url").asText();

            } else {
                log.error("이미지 업로드 실패: {}", response.getStatusCode());
                throw new ApplicationException(
                    ErrorStatus.toErrorStatus("이미지 업로드에 실패하였습니다.", 500, LocalDateTime.now()));
            }

        } catch (IOException e) {
            log.error("이미지 변환 중 오류 발생: {}", e.getMessage(), e);
            throw new ApplicationException(
                ErrorStatus.toErrorStatus("이미지 업로드에 실패하였습니다.", 500, LocalDateTime.now()));
        }
    }
}
