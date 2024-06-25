package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookTagResponse;

import java.util.List;

public interface BookTagService {

    List<BookTagResponse> getBookTagByBookId(Long bookId);
    BookTagResponse createBookTag(CreateBookTagRequest request);
    void removeBookTag(Long bookTagId);
}
