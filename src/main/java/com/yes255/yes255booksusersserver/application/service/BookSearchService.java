package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import com.yes255.yes255booksusersserver.presentation.dto.request.BookSearchRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookSearchService {

    Page<BookIndexResponse> searchBookByNamePaging(BookSearchRequest request, Pageable pageable);

    List<BookIndexResponse> searchBookByName(BookSearchRequest request);

    Page<BookIndexResponse> searchBookByDescription(BookSearchRequest request, Pageable pageable);

    Page<BookIndexResponse> searchBookByTagName(BookSearchRequest request, Pageable pageable);

    Page<BookIndexResponse> searchBookByAuthorName(BookSearchRequest request, Pageable pageable);

    Page<BookIndexResponse> searchAll(BookSearchRequest request, Pageable pageable);
}
