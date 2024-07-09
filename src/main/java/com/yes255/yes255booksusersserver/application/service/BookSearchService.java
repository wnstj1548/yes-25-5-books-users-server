package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookSearchService {

    Page<BookIndexResponse> searchBookByNamePaging(String keyword, Pageable pageable, String sortString);

    Page<BookIndexResponse> searchBookByDescription(String keyword, Pageable pageable, String sortString);

    Page<BookIndexResponse> searchBookByTagName(String keyword, Pageable pageable, String sortString);

    Page<BookIndexResponse> searchBookByAuthorName(String keyword, Pageable pageable, String sortString);

    Page<BookIndexResponse> searchBookByCategoryName(String keyword, Pageable pageable, String sortString);

    Page<BookIndexResponse> searchAll(String keyword, Pageable pageable, String sortString);
}
