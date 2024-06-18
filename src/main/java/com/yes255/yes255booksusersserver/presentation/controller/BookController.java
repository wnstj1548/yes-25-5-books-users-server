package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookCategoryService;
import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.application.service.BookTagService;
import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookCategoryService bookCategoryService;
    private final BookTagService bookTagService;

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> findAll() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> findById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.findBook(bookId));
    }

    @PostMapping("/books")
    public ResponseEntity<BookResponse> create(@RequestBody CreateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList ) {

        BookResponse response = bookService.createBook(request);

        for(Long categoryId : categoryIdList) {
            bookCategoryService.createBookCategory(response.bookId(), categoryId);
        }

        if(tagIdList != null) {
            for(Long tagId : tagIdList) {
                bookTagService.createBookTag(response.bookId(), tagId);
            }
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/books")
    public ResponseEntity<BookResponse> update(@RequestBody UpdateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList ) {

        List<BookCategoryResponse> bookCategoryList = bookCategoryService.findBookCategoryByBookId(request.bookId());
        List<BookTagResponse> bookTagList = bookTagService.findBookTagByBookId(request.bookId());

        for(BookCategoryResponse bookCategory : bookCategoryList) {
            bookCategoryService.deleteBookCategory(bookCategory.bookCategoryId());
        }

        for(BookTagResponse bookTag : bookTagList) {
            bookTagService.deleteBookTag(bookTag.bookTagId());
        }

        BookResponse response = bookService.updateBook(request);
        categoryIdList.forEach(categoryId -> bookCategoryService.createBookCategory(response.bookId(), categoryId));

        if(tagIdList != null) {
            for(Long tagId : tagIdList) {
                bookTagService.createBookTag(response.bookId(), tagId);
            }
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);

        return ResponseEntity.noContent().build();
    }
}
