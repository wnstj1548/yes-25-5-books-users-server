package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookTagService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
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

import java.time.LocalDateTime;
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
    public List<BookTagResponse> findBookTagByBookId(Long bookId) {
        return jpaBookTagRepository.findByBook(jpaBookRepository.findById(bookId).orElseThrow(() ->
                new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()))))
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public BookTagResponse createBookTag(Long bookId, Long tagId) {

        if (Objects.isNull(tagId) || Objects.isNull(bookId)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(ErrorStatus.toErrorStatus("책 값을 찾을 수 없습니다", 404, LocalDateTime.now())));
        Tag tag = jpaTagRepository.findById(tagId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("태그를 찾을 수 없습니다", 404, LocalDateTime.now())));

        if(Objects.isNull(book) || Objects.isNull(tag)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
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
    public void deleteBookTag(Long bookTagId) {

        if(!jpaBookTagRepository.existsById(bookTagId)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("알맞지 않은 북태그 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        jpaBookTagRepository.deleteById(bookTagId);
    }
}
