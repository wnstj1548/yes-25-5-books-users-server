package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByBook_BookIdAndIsActiveTrueOrderByReviewTimeDesc(Long bookId, Pageable pageable);

    List<Review> findAllByBook_BookIdAndIsActiveTrue(Long bookId);

    boolean existsByUser_UserIdAndBook_BookIdAndIsActiveTrue(Long userId, Long bookId);

    void deleteByReviewId(Long reviewId);

    boolean existsByUser_UserIdAndBook_BookId(long userId, long bookId);

    Page<Review> findAllByUser_UserIdOrderByReviewIdDesc(Long userId, Pageable pageable);
}
