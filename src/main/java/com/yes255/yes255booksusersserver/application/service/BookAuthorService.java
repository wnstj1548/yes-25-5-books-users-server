package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookAuthorRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookAuthorResponse;

import java.util.List;

public interface BookAuthorService {

    List<BookAuthorResponse> getBookAuthorByBookId(Long bookId);
    List<BookAuthorResponse> getBookAuthorByAuthorId(Long authorId);
    BookAuthorResponse createBookAuthor(CreateBookAuthorRequest request);
    void removeBookAuthor(Long bookAuthorId);
}
