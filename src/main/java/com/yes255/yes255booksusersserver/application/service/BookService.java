package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse createBook(CreateBookRequest createBookRequest);

    BookResponse findByBookId(long bookId);

    List<BookResponse> findAllBooks();

    BookResponse updateBook(UpdateBookRequest updateBookRequest);

    void deleteByBookId(long bookId);
}
