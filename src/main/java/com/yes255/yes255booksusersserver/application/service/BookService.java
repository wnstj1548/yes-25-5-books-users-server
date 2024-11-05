package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookQuantityRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCouponResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookOrderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BookService {

    BookResponse createBook(CreateBookRequest createBookRequest, List<Long> categoryIdList, List<Long> tagIdList);

    BookResponse getBook(long bookId);

    List<BookOrderResponse> getBooksByOrder(List<Long> bookIdList);

    Page<BookResponse> getAllBooks(Pageable pageable);

    List<BookResponse> getAllBooks();

    BookResponse updateBook(UpdateBookRequest updateBookRequest,  List<Long> categoryIdList, List<Long> tagIdList);

    void removeBook(Long bookId);

    List<BookResponse> getBookByCategoryId(Long categoryId);

    Page<BookResponse> getBookByCategoryId(Long categoryId, Pageable pageable, String sort);

    List<BookCouponResponse> getBookByName(String name);

    void addHitsCount(Long bookId);

    BookResponse getBookByIsbn(String isbn);

    void updateBookIsDeleteFalse(Long bookId);

    List<BookResponse> updateQuantity(UpdateBookQuantityRequest request);
}
