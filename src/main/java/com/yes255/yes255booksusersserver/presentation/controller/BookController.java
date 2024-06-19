
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

/**
 * 책에 관련된 API 처리를 하는 RestController
 */
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookCategoryService bookCategoryService;
    private final BookTagService bookTagService;

    /**
     * 모든 책을 가져옵니다.
     *
     * @return ResponseEntity<BookResponse> 형식의 모든 책 정보.
     */
    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> findAll() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    /**
     * 책 ID로 특정 책을 조회합니다.
     *
     * @param bookId 조회할 책의 ID
     * @return ResponseEntity<BookResponse> 형식의 특정 책 정보
     */
    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> findById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.findBook(bookId));
    }

    /**
     * 새로운 책을 생성합니다.
     *
     * @param request       생성할 책의 정보를 담은 CreateBookRequest 객체
     * @param categoryIdList 책과 연결될 카테고리 ID 목록
     * @param tagIdList     선택적으로 책과 연결될 태그 ID 목록
     * @param bindingResult 유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<BookResponse> 형식의 생성된 책 정보
     * @throws ValidationFailedException 요청에 유효성 검사 오류가 있는 경우 발생합니다.
     */
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

    /**
     * 기존 책을 업데이트합니다.
     *
     * @param request       업데이트할 책의 정보를 담은 UpdateBookRequest 객체
     * @param categoryIdList 업데이트된 책과 연결될 카테고리 ID 목록
     * @param tagIdList     선택적으로 업데이트된 책과 연결될 태그 ID 목록
     * @param bindingResult 유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<BookResponse> 형식의 업데이트된 책 정보
     * @throws ValidationFailedException 요청에 유효성 검사 오류가 있는 경우 발생합니다.
     */
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

    /**
     * 책의 수량을 업데이트합니다.
     *
     * @param bookId   업데이트할 책의 ID
     * @param quantity 새로운 수량 값
     * @return ResponseEntity<BookResponse> 형식의 업데이트된 책 정보
     * @throws QuantityInsufficientException 요청 수량이 재고보다 많은 경우 발생합니다.
     */
    @PatchMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> updateQuantity(@PathVariable Long bookId, @RequestParam(value = "quantity") Integer quantity) {

        BookResponse book = bookService.findBook(bookId);

        if(quantity > book.bookQuantity()) {
            throw new QuantityInsufficientException(ErrorStatus.toErrorStatus("주문 한 수량이 재고보다 많습니다.", 409, LocalDateTime.now()));
        }

        UpdateBookRequest updatedBook = UpdateBookRequest.builder()
                .bookId(book.bookId())
                .bookIsbn(book.bookIsbn())
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

    /**
     * 책을 삭제합니다.
     *
     * @param bookId 삭제할 책의 ID
     * @return 삭제 성공 여부를 나타내는 ResponseEntity
     */
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {

        bookService.deleteBook(bookId);

        return ResponseEntity.noContent().build();
    }
}
