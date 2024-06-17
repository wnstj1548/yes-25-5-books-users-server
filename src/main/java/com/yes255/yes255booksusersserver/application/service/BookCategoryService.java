package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;

import java.util.List;

public interface BookCategoryService {

    BookCategoryResponse createBookCategory(Long bookId, Long categoryId);

    BookCategoryResponse findById(Long bookCategoryId);

    List<BookCategoryResponse> findByBookId(Long bookId);

    List<BookCategoryResponse> findByCategoryId(Long categoryId);

    List<BookCategoryResponse> findAllBookCategories();

    BookCategoryResponse updateBookCategoryById(UpdateBookCategoryRequest request);

    void deleteByBookCategoryId(Long bookCategoryId);
}
