
package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookCategoryService;
import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.application.service.BookTagService;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookTagResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<BookResponse> create(@RequestBody @Valid CreateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

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
    public ResponseEntity<BookResponse> update(@RequestBody @Valid UpdateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList, BindingResult bindingResult ) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

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

    @PatchMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> updateQuantity(@PathVariable Long bookId, @RequestParam(value = "quantity") Integer quantity) {

        BookResponse book = bookService.findBook(bookId);

        if(quantity > book.bookQuantity()) {
            throw new QuantityInsufficientException(ErrorStatus.toErrorStatus("주문 한 수량이 재고보다 많습니다.", 409, LocalDateTime.now()));
        }

        UpdateBookRequest updatedBook = UpdateBookRequest.builder()
                .bookId(book.bookId())
                .bookName(book.bookName())
                .bookDescription(book.bookDescription())
                .bookAuthor(book.bookAuthor())
                .bookPublisher(book.bookPublisher())
                .bookPublishDate(book.bookPublishDate())
                .bookPrice(book.bookPrice())
                .bookSellingPrice(book.bookSellingPrice())
                .bookImage(book.bookImage())
                .quantity(book.bookQuantity() - quantity)
                .reviewCount(book.reviewCount())
                .hitsCount(book.hitsCount())
                .searchCount(book.searchCount())
                .build();

        return ResponseEntity.ok(bookService.updateBook(updatedBook));

    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {

        bookService.deleteBook(bookId);

        return ResponseEntity.noContent().build();
    }
}
