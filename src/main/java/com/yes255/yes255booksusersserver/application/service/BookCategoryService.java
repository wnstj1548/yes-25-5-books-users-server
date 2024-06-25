package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;

import java.util.List;

public interface BookCategoryService {

    BookCategoryResponse createBookCategory(Long bookId, Long categoryId);

    BookCategoryResponse getBookCategory(Long bookCategoryId);

    List<BookCategoryResponse> getBookCategoryByBookId(Long bookId);

    List<BookCategoryResponse> getBookCategoryByCategoryId(Long categoryId);

    List<BookCategoryResponse> getAllBookCategories();

    BookCategoryResponse updateBookCategory(UpdateBookCategoryRequest request);

    void removeBookCategory(Long bookCategoryId);
}
