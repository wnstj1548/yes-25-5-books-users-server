package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateBookResponse;

import java.util.List;

public interface BookService {

    BookResponse createBook(CreateBookRequest createBookRequest);

    BookResponse findById(long bookId);

    List<BookResponse> findAll();

    BookResponse updateBook(UpdateBookRequest updateBookRequest);

    void deleteById(long bookId);
}
