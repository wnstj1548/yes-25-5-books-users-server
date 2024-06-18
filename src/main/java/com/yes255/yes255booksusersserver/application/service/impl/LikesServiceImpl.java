package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.LikesService;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final JpaLikesRepository jpaLikesRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaBookRepository jpaBookRepository;

    public LikesResponse toResponse(Likes likes) {
        return LikesResponse.builder()
                .likesId(likes.getLikesId())
                .user(likes.getUser())
                .book(likes.getBook())
                .likesStatus(likes.isLikesStatus())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesResponse> findLikeByUserId(Long userId) {

        User user = jpaUserRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return jpaLikesRepository.findByUser(user).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesResponse> findLikeByBookId(Long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElse(null);
        if(book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        return jpaLikesRepository.findByBook(book).stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public LikesResponse createLike(CreateLikesRequest request) {

        Book book = jpaBookRepository.findById(request.bookId()).orElse(null);
        User user = jpaUserRepository.findById(request.userId()).orElse(null);

        if(book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Likes likes = Likes.builder()
                .likesId(null)
                .likesStatus(true)
                .user(user)
                .book(book)
                .build();

        jpaLikesRepository.save(likes);

        return toResponse(likes);
    }

    @Transactional
    @Override
    public LikesResponse updateLikeStatus(UpdateLikesRequest request) {

        Book book = jpaBookRepository.findById(request.bookId()).orElse(null);
        User user = jpaUserRepository.findById(request.userId()).orElse(null);

        if(book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Likes like = jpaLikesRepository.findByUserAndBook(user, book).orElse(null);

        if(like == null) {
            throw new IllegalArgumentException("Like not found");
        }

        Likes updatedLike = Likes.builder()
                .likesId(like.getLikesId())
                .likesStatus(!like.isLikesStatus())
                .user(like.getUser())
                .book(like.getBook())
                .build();

        return toResponse(jpaLikesRepository.save(updatedLike));
    }

}
