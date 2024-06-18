package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;

import java.util.List;

public interface LikesService {
    List<LikesResponse> findLikeByUserId(Long userId);
    List<LikesResponse> findLikeByBookId(Long bookId);
    LikesResponse createLike(CreateLikesRequest createLikesRequest);
    LikesResponse updateLikeStatus(UpdateLikesRequest updateLikesRequest);
}
