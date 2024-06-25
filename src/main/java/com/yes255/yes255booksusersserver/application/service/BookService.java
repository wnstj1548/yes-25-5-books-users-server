package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    BookResponse createBook(CreateBookRequest createBookRequest);

    BookResponse getBook(long bookId);

    Page<BookResponse> getAllBooks(Pageable pageable);

    List<BookResponse> getAllBooks();

    BookResponse updateBook(UpdateBookRequest updateBookRequest);

    void removeBook(Long bookId);

    List<BookResponse> getBookByCategoryId(Long categoryId);
}
