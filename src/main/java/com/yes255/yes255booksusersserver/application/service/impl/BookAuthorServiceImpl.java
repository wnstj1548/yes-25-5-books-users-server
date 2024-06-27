package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookAuthorService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookAuthorRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookAuthorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

    private final JpaBookAuthorRepository jpaBookAuthorRepository;
    private final JpaBookRepository jpaBookRepository;
    private final JpaAuthorRepository jpaAuthorRepository;

    @Transactional(readOnly = true)
    @Override
    public List<BookAuthorResponse> getBookAuthorByBookId(Long bookId) {

        return jpaBookAuthorRepository.findByBook(
                jpaBookRepository.findById(bookId)
                        .orElseThrow(() -> new ApplicationException(
                                ErrorStatus.toErrorStatus("해당 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
                        ))
        )
                .stream().map(BookAuthorResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookAuthorResponse> getBookAuthorByAuthorId(Long authorId) {

        return jpaBookAuthorRepository.findByAuthor(
                jpaAuthorRepository.findById(authorId)
                        .orElseThrow(() -> new ApplicationException(
                                ErrorStatus.toErrorStatus("해당 작가를 찾을 수 없습니다.", 404, LocalDateTime.now())
                        ))
        )
                .stream().map(BookAuthorResponse::fromEntity).toList();
    }

    @Transactional
    @Override
    public BookAuthorResponse createBookAuthor(CreateBookAuthorRequest request) {

        return BookAuthorResponse.fromEntity(
                jpaBookAuthorRepository.save(BookAuthor.builder()
                    .bookAuthorId(null)
                    .book(jpaBookRepository.findById(request.bookId())
                            .orElseThrow(() -> new ApplicationException(
                                    ErrorStatus.toErrorStatus("해당 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
                            )))
                    .author(jpaAuthorRepository.findById(request.authorId())
                            .orElseThrow(() -> new ApplicationException(
                                    ErrorStatus.toErrorStatus("해당 작가를 찾을 수 없습니다.", 404, LocalDateTime.now())
                            )))
                    .build())
                );
    }

    @Transactional
    @Override
    public void removeBookAuthor(Long bookAuthorId) {
        jpaBookAuthorRepository.deleteById(bookAuthorId);
    }
}
