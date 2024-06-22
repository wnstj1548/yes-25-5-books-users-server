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

    BookResponse findBook(long bookId);

    Page<BookResponse> findAllBooks(Pageable pageable);

    List<BookResponse> findAllBooks();

    BookResponse updateBook(UpdateBookRequest updateBookRequest);

    void deleteBook(Long bookId);

    List<BookResponse> findBookByCategoryId(Long categoryId);
}
