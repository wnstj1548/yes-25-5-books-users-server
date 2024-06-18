package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;

import java.util.List;

public interface BookCategoryService {

    BookCategoryResponse createBookCategory(Long bookId, Long categoryId);

    BookCategoryResponse findBookCategory(Long bookCategoryId);

    List<BookCategoryResponse> findBookCategoryByBookId(Long bookId);

    List<BookCategoryResponse> findBookCategoryByCategoryId(Long categoryId);

    List<BookCategoryResponse> findAllBookCategories();

    BookCategoryResponse updateBookCategory(UpdateBookCategoryRequest request);

    void deleteBookCategory(Long bookCategoryId);
}
