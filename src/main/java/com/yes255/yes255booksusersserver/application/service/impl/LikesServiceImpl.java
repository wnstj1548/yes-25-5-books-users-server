package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.LikesService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Likes;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaLikesRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final JpaLikesRepository jpaLikesRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaBookRepository jpaBookRepository;

    @Transactional(readOnly = true)
    @Override
    public List<LikesResponse> getLikeByUserId(Long userId) {

        User user = jpaUserRepository.findById(userId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("유저를 찾을 수 없습니다.", 404, LocalDateTime.now())));

        return jpaLikesRepository.findByUser(user).stream().map(LikesResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesResponse> getLikeByBookId(Long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("책을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        return jpaLikesRepository.findByBook(book).stream().map(LikesResponse::fromEntity).toList();
    }

    @Transactional
    @Override
    public LikesResponse createLike(CreateLikesRequest request) {

        Book book = jpaBookRepository.findById(request.bookId()).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("책을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        User user = jpaUserRepository.findById(request.userId()).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("유저를을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        Likes likes = Likes.builder()
                .likesId(null)
                .likesStatus(true)
                .user(user)
                .book(book)
                .build();

        jpaLikesRepository.save(likes);

        return LikesResponse.fromEntity(likes);
    }

    @Transactional
    @Override
    public LikesResponse updateLikeStatus(UpdateLikesRequest request) {

        Book book = jpaBookRepository.findById(request.bookId()).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("책을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        User user = jpaUserRepository.findById(request.userId()).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("유저를을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        Likes like = jpaLikesRepository.findByUserAndBook(user, book).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("좋아요를 찾을 수 없습니다.", 404, LocalDateTime.now())));

        Likes updatedLike = Likes.builder()
                .likesId(like.getLikesId())
                .likesStatus(!like.isLikesStatus())
                .user(like.getUser())
                .book(like.getBook())
                .build();

        return LikesResponse.fromEntity(jpaLikesRepository.save(updatedLike));
    }

}
