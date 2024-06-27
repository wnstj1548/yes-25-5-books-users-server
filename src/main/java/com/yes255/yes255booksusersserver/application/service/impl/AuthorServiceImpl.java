package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.AuthorService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Author;
import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookAuthorRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateAuthorRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.AuthorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final JpaAuthorRepository jpaAuthorRepository;
    private final JpaBookAuthorRepository jpaBookAuthorRepository;

    @Transactional(readOnly = true)
    @Override
    public AuthorResponse getAuthor(Long authorId) {

        Author author = jpaAuthorRepository.findById(authorId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorStatus.toErrorStatus("해당 작가를 찾을 수 없습니다.", 404, LocalDateTime.now())
                ));

        return AuthorResponse.fromEntity(author);
    }

    @Transactional(readOnly = true)
    @Override
    public AuthorResponse getAuthorByName(String authorName) {

        return AuthorResponse.fromEntity(
                jpaAuthorRepository.findByAuthorName(authorName)
                .orElseThrow(() -> new ApplicationException(
                        ErrorStatus.toErrorStatus("해당 작가를 찾을 수 없습니다.", 404, LocalDateTime.now())
                )));
    }

    @Transactional
    @Override
    public AuthorResponse createAuthor(CreateAuthorRequest request) {

        return AuthorResponse.fromEntity(jpaAuthorRepository.save(request.toEntity()));
    }

    @Transactional
    @Override
    public void removeAuthor(Long authorId) {

        List<BookAuthor> bookAuthorList = jpaBookAuthorRepository.findByAuthor(jpaAuthorRepository.findById(authorId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorStatus.toErrorStatus("해당 작가를 찾을 수 없습니다.", 404, LocalDateTime.now()))
                ));

        jpaBookAuthorRepository.deleteAll(bookAuthorList);

        jpaAuthorRepository.deleteById(authorId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isExistAuthorByName(String authorName) {
        return jpaAuthorRepository.findByAuthorName(authorName).isPresent();
    }
}
