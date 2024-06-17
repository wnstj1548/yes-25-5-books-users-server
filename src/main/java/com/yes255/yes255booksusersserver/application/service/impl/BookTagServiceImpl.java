package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookTagService;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookTagRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaTagRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookTagServiceImpl implements BookTagService {

    private final JpaBookTagRepository jpaBookTagRepository;
    private final JpaBookRepository jpaBookRepository;
    private final JpaTagRepository jpaTagRepository;

    public BookTagResponse toResponse(BookTag bookTag) {
        return BookTagResponse.builder()
                .bookTagId(bookTag.getBookTagId())
                .bookId(bookTag.getBook().getBookId())
                .tagId(bookTag.getTag().getTagId())
                .build();
    }

    @Transactional
    @Override
    public List<BookTagResponse> findByBookId(Long bookId) {
        return jpaBookTagRepository.findByBook(jpaBookRepository.findById(bookId).orElse(null)).stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public BookTagResponse createBookTag(Long bookId, Long tagId) {

        if (Objects.isNull(tagId) || Objects.isNull(bookId)) {
            throw new IllegalArgumentException("Book id and tag id cannot be null");
        }

        Book book = jpaBookRepository.findById(bookId).orElse(null);
        Tag tag = jpaTagRepository.findById(tagId).orElse(null);

        if(Objects.isNull(book) || Objects.isNull(tag)) {
            throw new IllegalArgumentException("Book id and tag id cannot be null");
        }

        BookTag bookTag = BookTag.builder()
                .bookTagId(null)
                .book(book)
                .tag(tag)
                .build();

        return toResponse(jpaBookTagRepository.save(bookTag));

    }

    @Transactional
    @Override
    public void deleteByBookTagId(Long bookTagId) {

        if(jpaBookTagRepository.existsById(bookTagId)) {
            throw new IllegalArgumentException("알맞지 않은 bookTagId 입니다.");
        }

        jpaBookTagRepository.deleteById(bookTagId);
    }
}
