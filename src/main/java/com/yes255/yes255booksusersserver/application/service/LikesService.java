package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikesService {
    List<LikesResponse> getLikeByUserId(Long userId);
    List<LikesResponse> getLikeByBookId(Long bookId);
    LikesResponse createLike(Long bookId, Long userId);
    LikesResponse updateLikeStatus(Long bookId, Long userId);
    boolean isExistByBookIdAndUserId(Long bookId, Long userId);
    LikesResponse getLikeByBookIdAndUserId(Long bookId, Long userId);
}
