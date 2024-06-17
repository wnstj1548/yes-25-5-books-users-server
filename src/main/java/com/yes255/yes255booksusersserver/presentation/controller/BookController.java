package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookCategoryService;
import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookCategoryService bookCategoryService;

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("bookId") long bookId) {
        return ResponseEntity.ok(bookService.findByBookId(bookId));
    }

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList ) {

        BookResponse response = bookService.createBook(request);

        for(Long categoryId : categoryIdList) {
            bookCategoryService.createBookCategory(response.bookId(), categoryId);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/books")
    public ResponseEntity<BookResponse> updateBook(@RequestBody UpdateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList ) {

        List<BookCategoryResponse> bookCategoryList = bookCategoryService.findByBookId(request.bookId());

        for(BookCategoryResponse bookCategory : bookCategoryList) {
            bookCategoryService.deleteByBookCategoryId(bookCategory.bookCategoryId());
        }

        BookResponse response = bookService.updateBook(request);

        for(Long categoryId : categoryIdList) {
            bookCategoryService.createBookCategory(response.bookId(), categoryId);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteByBookId(bookId);

        return ResponseEntity.noContent().build();
    }
}
